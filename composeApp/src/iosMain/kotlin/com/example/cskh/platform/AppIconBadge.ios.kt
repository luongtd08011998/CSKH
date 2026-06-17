package com.example.cskh.platform

import platform.UserNotifications.UNUserNotificationCenter
import kotlinx.cinterop.ExperimentalForeignApi

actual fun createAppIconBadge(): AppIconBadge = IosAppIconBadge()

@OptIn(ExperimentalForeignApi::class)
private class IosAppIconBadge : AppIconBadge {
    override fun setCount(count: Int) {
        // iOS 16+: dùng UNUserNotificationCenter.setBadgeCount
        // iOS 15 trở xuống: dùng UIApplication.shared.applicationIconBadgeNumber
        val safeCount = count.coerceAtLeast(0)
        UNUserNotificationCenter.currentNotificationCenter()
            .setBadgeCount(safeCount.toLong(), withCompletionHandler = null)
    }
}
