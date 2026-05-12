package com.example.cskh.di

import com.example.cskh.fcm.FcmDeviceSyncAndroid
import com.example.cskh.platform.FcmDeviceSync
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidFcmKoinModule = module {
    single<FcmDeviceSync> {
        FcmDeviceSyncAndroid(get(), get(), get(), get(), androidContext())
    }
}
