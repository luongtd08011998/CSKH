package com.example.cskh.fcm

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.cskh.data.session.SessionManager
import com.example.cskh.domain.usecase.RegisterFcmDeviceUseCase
import com.example.cskh.domain.usecase.UnregisterFcmDeviceUseCase
import com.example.cskh.domain.usecase.UserFormPreferencesUseCase
import com.example.cskh.platform.FcmDeviceSync
import com.example.cskh.platform.defaultDevMachineApiBaseUrl
import com.example.cskh.presentation.CompanyBranding
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.random.Random

class FcmDeviceSyncAndroid(
    private val registerFcmDevice: RegisterFcmDeviceUseCase,
    private val unregisterFcmDevice: UnregisterFcmDeviceUseCase,
    private val sessionManager: SessionManager,
    private val formPreferences: UserFormPreferencesUseCase,
    private val appContext: Context,
) : FcmDeviceSync {

    private fun resolveBaseUrl(): String {
        return formPreferences.getBaseUrl().ifBlank {
            defaultDevMachineApiBaseUrl(CompanyBranding.DEV_API_PORT)
        }
    }

    override suspend fun registerIfLoggedIn() = withContext(Dispatchers.IO) {
        Log.d(TAG, "=== registerIfLoggedIn START ===")
        if (sessionManager.accessToken.isNullOrBlank()) {
            Log.w(TAG, "registerIfLoggedIn: no access token, skip")
            return@withContext
        }
        if (!isPlayServicesAvailable()) {
            Log.e(TAG, "Google Play Services not available, skip register")
            return@withContext
        }
        Log.d(TAG, "Play Services OK, fetching FCM token...")
        val fcm = fetchFcmTokenWithRetry()
        if (fcm == null) {
            Log.e(TAG, "FCM token is null after retries, scheduling WorkManager fallback in 30s")
            scheduleWorkManagerFallback()
            return@withContext
        }
        Log.d(TAG, "FCM token OK: ${fcm.take(20)}..., calling registerWithFcmToken")
        registerWithFcmToken(fcm)
        val subResult = runCatching { subscribeGeneralNewsTopic() }
        if (subResult.isFailure) {
            Log.w(TAG, "subscribe topic failed: ${subResult.exceptionOrNull()?.message}")
        } else {
            Log.d(TAG, "subscribe topic general_news OK")
        }
        Log.d(TAG, "=== registerIfLoggedIn END ===")
    }

    override suspend fun registerWithFcmToken(fcmToken: String) {
        withContext(Dispatchers.IO) {
            if (fcmToken.isBlank()) {
                Log.w(TAG, "registerWithFcmToken: token is blank, skip")
                return@withContext
            }
            if (sessionManager.accessToken.isNullOrBlank()) {
                Log.w(TAG, "registerWithFcmToken: no access token, skip")
                return@withContext
            }
            val base = resolveBaseUrl()
            Log.d(TAG, "registerWithFcmToken: calling API $base .../device/register")
            val result = runCatching { registerFcmDevice.register(base, fcmToken) }
            if (result.isFailure) {
                Log.e(TAG, "register API failed: ${result.exceptionOrNull()?.message}", result.exceptionOrNull())
            } else {
                Log.d(TAG, "register API success → customer_device created")
            }
        }
    }

    override suspend fun unregisterIfLoggedIn(): Unit = withContext(Dispatchers.IO) {
        Log.d(TAG, "=== unregisterIfLoggedIn START ===")
        if (sessionManager.accessToken.isNullOrBlank()) {
            Log.w(TAG, "unregisterIfLoggedIn: no access token, skip")
            return@withContext
        }
        if (!isPlayServicesAvailable()) {
            Log.e(TAG, "Google Play Services not available, skip unregister")
            return@withContext
        }
        val fcm = fetchFcmTokenWithRetry()
        if (fcm.isNullOrBlank()) {
            Log.e(TAG, "FCM token is null after retries, abort unregister")
            return@withContext
        }
        val base = resolveBaseUrl()
        Log.d(TAG, "unregister: calling API $base .../device/unregister")
        val result = runCatching { unregisterFcmDevice.unregister(base, fcm) }
        if (result.isFailure) {
            Log.e(TAG, "unregister API failed: ${result.exceptionOrNull()?.message}", result.exceptionOrNull())
        } else {
            Log.d(TAG, "unregister API success → customer_device deleted")
        }
        Log.d(TAG, "=== unregisterIfLoggedIn END ===")
    }

    private fun scheduleWorkManagerFallback() {
        val workManager = WorkManager.getInstance(appContext)
        val request = OneTimeWorkRequestBuilder<FcmRegisterWorker>()
            .setInitialDelay(30, TimeUnit.SECONDS)
            .addTag(FcmRegisterWorker.WORK_NAME)
            .build()
        workManager.enqueue(request)
        Log.d(TAG, "WorkManager fallback scheduled in 30s")
    }

    private suspend fun fetchFcmTokenWithRetry(): String? {
        val delays = listOf(2_000L, 5_000L, 10_000L)
        delays.forEachIndexed { attempt, delayMs ->
            val result = runCatching { fetchFcmToken() }
            result.onSuccess {
                Log.d(TAG, "fetchFcmToken success on attempt ${attempt + 1}")
                return it
            }
            Log.w(TAG, "fetchFcmToken attempt ${attempt + 1}/${delays.size} failed: ${result.exceptionOrNull()?.message}")
            val jitter = Random.nextLong(0, 500)
            Log.d(TAG, "retrying in ${delayMs + jitter}ms...")
            delay(delayMs + jitter)
        }
        Log.e(TAG, "fetchFcmToken failed after ${delays.size} retries")
        return null
    }

    private fun isPlayServicesAvailable(): Boolean {
        val status = GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(appContext)
        if (status != com.google.android.gms.common.ConnectionResult.SUCCESS) {
            Log.w(TAG, "Play Services unavailable, status=$status")
            return false
        }
        return true
    }

    private suspend fun fetchFcmToken(): String = suspendCancellableCoroutine { cont ->
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { cont.resume(it) }
            .addOnFailureListener { e ->
                if (cont.isCancelled) return@addOnFailureListener
                cont.resumeWithException(e)
            }
    }

    private suspend fun subscribeGeneralNewsTopic() = suspendCancellableCoroutine { cont ->
        FirebaseMessaging.getInstance().subscribeToTopic("general_news")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    cont.resume(Unit)
                } else {
                    cont.resumeWithException(task.exception ?: Exception("Subscribe topic failed"))
                }
            }
    }

    companion object {
        private const val TAG = "FCM"
    }
}
