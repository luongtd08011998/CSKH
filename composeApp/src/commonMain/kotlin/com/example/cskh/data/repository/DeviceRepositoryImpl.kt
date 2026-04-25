package com.example.cskh.data.repository

import com.example.cskh.data.remote.dto.DeviceRegisterRequestDto
import com.example.cskh.domain.repository.DeviceRepository
import com.example.cskh.util.normalizeApiBaseUrl
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class DeviceRepositoryImpl(
    private val client: HttpClient,
) : DeviceRepository {

    override suspend fun registerDevice(
        baseUrl: String,
        deviceToken: String,
        accessToken: String,
    ): Result<Unit> = runCatching {
        val url = "${normalizeApiBaseUrl(baseUrl)}/api/v1/qlkh/customer/device/register"
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $accessToken")
            setBody(DeviceRegisterRequestDto(deviceToken = deviceToken))
        }
        if (response.status.value !in 200..299) {
            val text = runCatching { response.bodyAsText() }.getOrNull()
            error(text ?: "HTTP ${response.status.value}")
        }
    }
}
