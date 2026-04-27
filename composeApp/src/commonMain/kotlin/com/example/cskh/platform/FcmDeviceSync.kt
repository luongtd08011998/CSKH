package com.example.cskh.platform

/**
 * Android: đăng ký FCM token lên backend. Các nền tảng khác: no-op.
 */
interface FcmDeviceSync {
    suspend fun registerIfLoggedIn()
    suspend fun registerWithFcmToken(fcmToken: String)
    suspend fun unregisterIfLoggedIn()
}

object FcmDeviceSyncNoop : FcmDeviceSync {
    override suspend fun registerIfLoggedIn() {}
    override suspend fun registerWithFcmToken(fcmToken: String) {}
    override suspend fun unregisterIfLoggedIn() {}
}
