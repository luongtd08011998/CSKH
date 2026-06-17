package com.example.cskh.platform

import com.example.cskh.data.session.SessionManager
import com.example.cskh.domain.usecase.RegisterFcmDeviceUseCase
import com.example.cskh.domain.usecase.UnregisterFcmDeviceUseCase
import com.example.cskh.domain.usecase.UserFormPreferencesUseCase
import com.example.cskh.presentation.CompanyBranding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import platform.Foundation.NSLog

class FcmDeviceSyncIos(
    private val registerFcmDevice: RegisterFcmDeviceUseCase,
    private val unregisterFcmDevice: UnregisterFcmDeviceUseCase,
    private val sessionManager: SessionManager,
    private val formPreferences: UserFormPreferencesUseCase,
) : FcmDeviceSync {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Flag: chỉ đăng ký listener 1 lần — tránh re-register mỗi lần login
    private var listenerRegistered = false

    private fun resolveBaseUrl(): String {
        return formPreferences.getBaseUrl().ifBlank {
            defaultDevMachineApiBaseUrl(CompanyBranding.DEV_API_PORT)
        }
    }

    override suspend fun registerIfLoggedIn() = withContext(Dispatchers.IO) {
        NSLog("FCM_IOS: registerIfLoggedIn START")
        if (sessionManager.accessToken.isNullOrBlank()) {
            NSLog("FCM_IOS: no access token, skip")
            return@withContext
        }

        // Chỉ đăng ký listener 1 lần — tránh duplicate calls khi login nhiều lần
        if (!listenerRegistered) {
            listenerRegistered = true
            IosFcmTokenBridge.setOnTokenChangedListener { token ->
                NSLog("FCM_IOS: token received via listener: ${token.take(20)}...")
                scope.launch(Dispatchers.IO) {
                    registerWithFcmToken(token)
                }
            }
        }

        // Nếu đã có token lưu sẵn, gửi ngay (listener đã set ở trên không gọi lại nữa)
        val existingToken = IosFcmTokenBridge.getSavedFcmToken()
        if (!existingToken.isNullOrBlank()) {
            NSLog("FCM_IOS: found existing token: ${existingToken.take(20)}...")
            registerWithFcmToken(existingToken)
        } else {
            NSLog("FCM_IOS: no token yet, waiting for Firebase to provide one...")
        }

        NSLog("FCM_IOS: registerIfLoggedIn END")
    }

    override suspend fun registerWithFcmToken(fcmToken: String) {
        withContext(Dispatchers.IO) {
            if (fcmToken.isBlank()) {
                NSLog("FCM_IOS: registerWithFcmToken: token is blank, skip")
                return@withContext
            }
            if (sessionManager.accessToken.isNullOrBlank()) {
                NSLog("FCM_IOS: registerWithFcmToken: no access token, skip")
                return@withContext
            }

            val base = resolveBaseUrl()
            NSLog("FCM_IOS: calling API $base .../device/register")
            val result = runCatching { registerFcmDevice.register(base, fcmToken) }
            if (result.isFailure) {
                NSLog("FCM_IOS: register API failed: ${result.exceptionOrNull()?.message}")
            } else {
                NSLog("FCM_IOS: register API success")
            }
        }
    }

    override suspend fun unregisterIfLoggedIn() = withContext(Dispatchers.IO) {
        NSLog("FCM_IOS: unregisterIfLoggedIn START")
        if (sessionManager.accessToken.isNullOrBlank()) {
            NSLog("FCM_IOS: no access token, skip unregister")
            return@withContext
        }

        val token = IosFcmTokenBridge.getSavedFcmToken()
        if (token.isNullOrBlank()) {
            NSLog("FCM_IOS: no saved token, skip unregister")
            return@withContext
        }

        val base = resolveBaseUrl()
        NSLog("FCM_IOS: calling API $base .../device/unregister")
        val result = runCatching { unregisterFcmDevice.unregister(base, token) }
        if (result.isFailure) {
            NSLog("FCM_IOS: unregister API failed: ${result.exceptionOrNull()?.message}")
        } else {
            NSLog("FCM_IOS: unregister API success")
            IosFcmTokenBridge.clearFcmToken()
        }
        NSLog("FCM_IOS: unregisterIfLoggedIn END")
    }

}
