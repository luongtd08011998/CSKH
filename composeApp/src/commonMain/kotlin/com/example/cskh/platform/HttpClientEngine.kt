package com.example.cskh.platform

import io.ktor.client.engine.HttpClientEngine

expect fun createHttpClientEngine(): HttpClientEngine

expect fun createTrustAllEngine(): HttpClientEngine
