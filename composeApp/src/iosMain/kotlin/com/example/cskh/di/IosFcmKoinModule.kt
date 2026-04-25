package com.example.cskh.di

import com.example.cskh.platform.FcmDeviceSync
import com.example.cskh.platform.FcmDeviceSyncNoop
import org.koin.dsl.module

val iosFcmKoinModule = module {
    single<FcmDeviceSync> { FcmDeviceSyncNoop }
}
