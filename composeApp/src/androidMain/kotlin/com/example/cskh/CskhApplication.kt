package com.example.cskh

import android.app.Application
import com.example.cskh.di.androidFcmKoinModule
import com.example.cskh.di.appModule
import com.example.cskh.platform.FcmDeviceSync
import com.example.cskh.platform.AndroidApplicationHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class CskhApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        AndroidApplicationHolder.init(this)
        startKoin {
            androidContext(this@CskhApplication)
            modules(appModule, androidFcmKoinModule)
        }
        applicationScope.launch {
            GlobalContext.get().get<FcmDeviceSync>().registerIfLoggedIn()
        }
    }
}
