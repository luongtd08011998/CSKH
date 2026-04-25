package com.example.cskh.di

import androidx.compose.runtime.Composable

@Composable
expect fun CskhKoinHost(content: @Composable () -> Unit)
