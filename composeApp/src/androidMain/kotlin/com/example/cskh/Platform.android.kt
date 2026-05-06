package com.example.cskh

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val platformType: String = "ANDROID"
}

actual fun getPlatform(): Platform = AndroidPlatform()