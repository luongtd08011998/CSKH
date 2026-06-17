package com.example.cskh.di

import com.example.cskh.platform.FcmDeviceSync
import com.example.cskh.platform.FcmDeviceSyncIos
import org.koin.dsl.module

val iosFcmKoinModule = module {
    single<FcmDeviceSync> {
        FcmDeviceSyncIos(get(), get(), get(), get())
    }
}
