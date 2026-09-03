package com.example.cskh

interface Platform {
    val name: String
    val platformType: String
}

expect fun getPlatform(): Platform

/**
 * Trả về versionCode (Android) hoặc CFBundleVersion (iOS) dưới dạng Int.
 * Dùng để so sánh với minRequiredVersionCode từ Backend cho tính năng Force Update.
 */
expect fun getAppVersionCode(): Int