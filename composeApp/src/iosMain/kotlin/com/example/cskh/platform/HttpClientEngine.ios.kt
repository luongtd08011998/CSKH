package com.example.cskh.platform

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

actual fun createHttpClientEngine(): HttpClientEngine = Darwin.create()

actual fun createTrustAllEngine(): HttpClientEngine = Darwin.create()
