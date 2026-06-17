package com.example.cskh.platform

import platform.Foundation.NSUserDefaults
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue


/**
 * Bridge để Swift (iOSApp.swift) truyền FCM token sang Kotlin.
 * Swift gọi [saveFcmToken] khi nhận được token từ Firebase Messaging.
 * Kotlin (FcmDeviceSyncIos) đọc token qua [getSavedFcmToken].
 */
object IosFcmTokenBridge {

    private const val KEY_FCM_TOKEN = "fcm_device_token"

    private var onTokenChanged: ((String) -> Unit)? = null

    /**
     * Đăng ký callback khi FCM token thay đổi.
     * Dùng để tự động gửi token lên server ngay khi nhận được.
     */
    fun setOnTokenChangedListener(listener: ((String) -> Unit)?) {
        onTokenChanged = listener
        // Không auto-invoke ở đây — FcmDeviceSyncIos tự đọc existingToken sau khi set listener
        // để tránh gọi API register 2 lần trong lần login đầu
    }

    /**
     * Gọi từ Swift khi Firebase Messaging trả về FCM token.
     */
    fun saveFcmToken(token: String) {
        if (token.isBlank()) return
        NSUserDefaults.standardUserDefaults.setObject(token, forKey = KEY_FCM_TOKEN)
        dispatch_async(dispatch_get_main_queue()) {
            onTokenChanged?.invoke(token)
        }
    }

    /**
     * Đọc FCM token đã lưu.
     */
    fun getSavedFcmToken(): String? {
        return NSUserDefaults.standardUserDefaults.stringForKey(KEY_FCM_TOKEN)
    }

    /**
     * Xóa token khi logout.
     */
    fun clearFcmToken() {
        NSUserDefaults.standardUserDefaults.removeObjectForKey(KEY_FCM_TOKEN)
    }
}

/**
 * Top-level wrapper để Swift gọi qua `IosFcmTokenBridgeKt.saveFcmToken(token:)`.
 * Kotlin/Native chỉ export top-level functions sang ObjC/Swift với tên FileKt.
 */
fun saveFcmToken(token: String) = IosFcmTokenBridge.saveFcmToken(token)
