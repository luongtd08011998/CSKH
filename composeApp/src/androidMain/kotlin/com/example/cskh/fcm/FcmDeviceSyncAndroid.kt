package com.example.cskh.fcm

import com.example.cskh.data.session.SessionManager
import com.example.cskh.domain.usecase.RegisterFcmDeviceUseCase
import com.example.cskh.domain.usecase.UnregisterFcmDeviceUseCase
import com.example.cskh.domain.usecase.UserFormPreferencesUseCase
import com.example.cskh.platform.FcmDeviceSync
import com.example.cskh.platform.defaultDevMachineApiBaseUrl
import com.example.cskh.presentation.CompanyBranding
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FcmDeviceSyncAndroid(
    private val registerFcmDevice: RegisterFcmDeviceUseCase,
    private val unregisterFcmDevice: UnregisterFcmDeviceUseCase,
    private val sessionManager: SessionManager,
    private val formPreferences: UserFormPreferencesUseCase,
) : FcmDeviceSync {

    private fun resolveBaseUrl(): String {
        return formPreferences.getBaseUrl().ifBlank {
            defaultDevMachineApiBaseUrl(CompanyBranding.DEV_API_PORT)
        }
    }

    override suspend fun registerIfLoggedIn() = withContext(Dispatchers.IO) {
        if (sessionManager.accessToken.isNullOrBlank()) return@withContext
        val fcm = runCatching { fetchFcmToken() }.getOrNull() ?: return@withContext
        registerWithFcmToken(fcm)
        runCatching { subscribeGeneralNewsTopic() }.getOrNull()
    }

    override suspend fun registerWithFcmToken(fcmToken: String) {
        withContext(Dispatchers.IO) {
            if (fcmToken.isBlank()) return@withContext
            if (sessionManager.accessToken.isNullOrBlank()) return@withContext
            val base = resolveBaseUrl()
            val result = runCatching { registerFcmDevice.register(base, fcmToken) }
            result.exceptionOrNull()?.let { e ->
                android.util.Log.e("FCM_REG", "register failed: ${e.message}", e)
            }
        }
    }

    override suspend fun unregisterIfLoggedIn(): Unit = withContext(Dispatchers.IO) {
        if (sessionManager.accessToken.isNullOrBlank()) return@withContext
        val fcm = runCatching { fetchFcmToken() }.getOrNull() ?: return@withContext
        if (fcm.isBlank()) return@withContext
        val base = resolveBaseUrl()
        val result = runCatching { unregisterFcmDevice.unregister(base, fcm) }
        result.exceptionOrNull()?.let { e ->
            android.util.Log.e("FCM_UNREG", "unregister failed: ${e.message}", e)
        }
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
}
