package com.example.cskh.platform

/** Set số badge trên icon app ngoài màn hình chính (iOS) hoặc no-op (Android). */
interface AppIconBadge {
    fun setCount(count: Int)
}

expect fun createAppIconBadge(): AppIconBadge
