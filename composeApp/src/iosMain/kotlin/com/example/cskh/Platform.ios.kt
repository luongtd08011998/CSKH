package com.example.cskh

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val platformType: String = "IOS"
}

actual fun getPlatform(): Platform = IOSPlatform()