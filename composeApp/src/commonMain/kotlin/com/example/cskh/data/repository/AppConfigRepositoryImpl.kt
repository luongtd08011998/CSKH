package com.example.cskh.data.repository

import com.example.cskh.data.remote.dto.AppConfigResponseDto
import com.example.cskh.domain.model.AppConfig
import com.example.cskh.domain.repository.AppConfigRepository
import com.example.cskh.util.normalizeApiBaseUrl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText

class AppConfigRepositoryImpl(
    private val client: HttpClient,
) : AppConfigRepository {

    override suspend fun getLatestConfig(baseUrl: String, platform: String): Result<AppConfig> =
        runCatching {
            val url = "${normalizeApiBaseUrl(baseUrl)}/api/v1/app-configs/latest"
            val response = client.get(url) {
                parameter("platform", platform)
            }
            if (response.status.value !in 200..299) {
                val text = runCatching { response.bodyAsText() }.getOrNull()
                val errorMessage = text?.let {
                    runCatching {
                        kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
                            .decodeFromString<com.example.cskh.data.remote.dto.BaseErrorResponse>(it).message
                    }.getOrNull()
                }
                error(errorMessage ?: text ?: "HTTP ${response.status.value}")
            }
            val envelope = response.body<AppConfigResponseDto>()
            envelope.data?.toDomain() ?: error(envelope.message ?: "Không nhận được dữ liệu cấu hình")
        }
}
