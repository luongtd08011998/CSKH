package com.example.cskh.data.remote

import com.example.cskh.data.remote.dto.AuthResponseDto
import com.example.cskh.data.remote.dto.RefreshRequestDto
import com.example.cskh.data.session.SessionManager
import com.example.cskh.platform.createHttpClientEngine
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Tạo HttpClient cơ bản (không có auth interceptor).
 * Dùng cho các request không cần token (ví dụ /auth/login, /auth/refresh).
 */
fun createAppHttpClient(json: Json): HttpClient {
    val engine = createHttpClientEngine()
    return HttpClient(engine) {
        install(ContentNegotiation) {
            json(json)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 60_000
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 60_000
        }
    }
}

/**
 * Thực hiện refresh token thủ công (không qua interceptor để tránh vòng lặp).
 * Trả về accessToken mới, hoặc null nếu refresh thất bại / hết hạn.
 *
 * Caller (ViewModel / UseCase) sẽ kiểm tra null và điều hướng về màn hình Login.
 */
suspend fun tryRefreshToken(
    client: HttpClient,
    baseUrl: String,
    refreshToken: String,
    sessionManager: SessionManager,
    onRefreshSuccess: (newAccessToken: String, sameRefreshToken: String) -> Unit,
): Boolean {
    return runCatching {
        val url = "$baseUrl/api/v1/qlkh/auth/refresh"
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(RefreshRequestDto(refreshToken = refreshToken))
        }
        if (response.status.value == 401) return false
        if (response.status.value !in 200..299) return false

        val envelope = response.body<AuthResponseDto>()
        val data = envelope.data ?: return false

        val newAccess = data.accessToken
        val sameRefresh = data.refreshToken.ifBlank { refreshToken }
        sessionManager.updateAccessToken(newAccess)
        onRefreshSuccess(newAccess, sameRefresh)
        true
    }.getOrDefault(false)
}
