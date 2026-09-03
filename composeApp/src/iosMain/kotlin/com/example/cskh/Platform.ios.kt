package com.example.cskh

import platform.Foundation.NSBundle
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val platformType: String = "IOS"
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun getAppVersionCode(): Int {
    // iOS dùng CFBundleVersion (build number) để so sánh
    val versionString = NSBundle.mainBundle.infoDictionary
        ?.get("CFBundleVersion") as? String
    return versionString?.toIntOrNull() ?: 0
}