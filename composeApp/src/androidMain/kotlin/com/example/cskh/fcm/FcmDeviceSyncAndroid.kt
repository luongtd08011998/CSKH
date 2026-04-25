package com.example.cskh.fcm

import com.example.cskh.data.session.SessionManager
import com.example.cskh.domain.usecase.RegisterFcmDeviceUseCase
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
    }

    override suspend fun registerWithFcmToken(fcmToken: String) {
        withContext(Dispatchers.IO) {
            if (fcmToken.isBlank()) return@withContext
            if (sessionManager.accessToken.isNullOrBlank()) return@withContext
            val base = resolveBaseUrl()
            runCatching { registerFcmDevice.register(base, fcmToken) }
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
}
