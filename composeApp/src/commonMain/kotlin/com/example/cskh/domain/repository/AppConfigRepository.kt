package com.example.cskh.domain.repository

import com.example.cskh.domain.model.AppConfig

interface AppConfigRepository {
    suspend fun getLatestConfig(baseUrl: String, platform: String): Result<AppConfig>
}
