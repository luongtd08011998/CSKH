package com.example.cskh.di

import androidx.compose.runtime.Composable
import org.koin.compose.KoinApplication

@Composable
actual fun CskhKoinHost(content: @Composable () -> Unit) {
    KoinApplication(application = { modules(appModule, iosFcmKoinModule) }) { content() }
}
