package com.example.cskh

import android.os.Build
import com.example.cskh.platform.AndroidApplicationHolder

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val platformType: String = "ANDROID"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun getAppVersionCode(): Int {
    val context = AndroidApplicationHolder.application ?: return 0
    return try {
        val info = context.packageManager.getPackageInfo(context.packageName, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            info.longVersionCode.toInt()
        } else {
            @Suppress("DEPRECATION")
            info.versionCode
        }
    } catch (e: Exception) {
        0
    }
}