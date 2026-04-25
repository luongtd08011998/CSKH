package com.example.cskh.di

import androidx.compose.runtime.Composable
import org.koin.compose.KoinContext

@Composable
actual fun CskhKoinHost(content: @Composable () -> Unit) {
    KoinContext { content() }
}
