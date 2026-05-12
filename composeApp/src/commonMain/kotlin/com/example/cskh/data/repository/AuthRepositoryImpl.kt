package com.example.cskh.data.repository

import com.example.cskh.data.remote.dto.AuthResponseDto
import com.example.cskh.data.remote.dto.LoginRequestDto
import com.example.cskh.data.remote.dto.RefreshRequestDto
import com.example.cskh.data.session.SessionManager
import com.example.cskh.domain.repository.AuthRepository
import com.example.cskh.util.normalizeApiBaseUrl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class AuthRepositoryImpl(
    private val client: HttpClient,
    private val sessionManager: SessionManager,
) : AuthRepository {

    override suspend fun login(
        baseUrl: String,
        digiCode: String,
        phone: String,
    ): Result<Pair<String, String>> = runCatching {
        val url = "${normalizeApiBaseUrl(baseUrl)}/api/v1/qlkh/auth/login"
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(LoginRequestDto(digiCode = digiCode, phone = phone))
        }
        if (response.status.value !in 200..299) {
            val text = runCatching { response.bodyAsText() }.getOrNull()
            val errorMessage = text?.let {
                runCatching {
                    kotlinx.serialization.json.Json { ignoreUnknownKeys = true }.decodeFromString<com.example.cskh.data.remote.dto.BaseErrorResponse>(it).message
                }.getOrNull()
            }
            error(errorMessage ?: text ?: "HTTP ${response.status.value}")
        }
        val envelope = response.body<AuthResponseDto>()
        val data = envelope.data ?: error(envelope.message ?: "Không nhận được token")
        Pair(data.accessToken, data.refreshToken)
    }

    override suspend fun refresh(
        baseUrl: String,
        refreshToken: String,
    ): Result<Pair<String, String>> = runCatching {
        val url = "${normalizeApiBaseUrl(baseUrl)}/api/v1/qlkh/auth/refresh"
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(RefreshRequestDto(refreshToken = refreshToken))
        }
        if (response.status.value == 401) {
            error("REFRESH_EXPIRED") // caller sẽ điều hướng về Login
        }
        if (response.status.value !in 200..299) {
            val text = runCatching { response.bodyAsText() }.getOrNull()
            val errorMessage = text?.let {
                runCatching {
                    kotlinx.serialization.json.Json { ignoreUnknownKeys = true }.decodeFromString<com.example.cskh.data.remote.dto.BaseErrorResponse>(it).message
                }.getOrNull()
            }
            error(errorMessage ?: text ?: "HTTP ${response.status.value}")
        }
        val envelope = response.body<AuthResponseDto>()
        val data = envelope.data ?: error(envelope.message ?: "Không nhận được token mới")
        // refreshToken giữ nguyên theo spec; trả về token server trả về (hoặc refreshToken cũ)
        Pair(data.accessToken, data.refreshToken.ifBlank { refreshToken })
    }

    override suspend fun logout(baseUrl: String): Result<Unit> = runCatching {
        val token = sessionManager.accessToken ?: return@runCatching
        val url = "${normalizeApiBaseUrl(baseUrl)}/api/v1/qlkh/auth/logout"
        val response = client.post(url) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        if (response.status.value !in 200..299) {
            val text = runCatching { response.bodyAsText() }.getOrNull()
            val errorMessage = text?.let {
                runCatching {
                    kotlinx.serialization.json.Json { ignoreUnknownKeys = true }.decodeFromString<com.example.cskh.data.remote.dto.BaseErrorResponse>(it).message
                }.getOrNull()
            }
            error(errorMessage ?: text ?: "HTTP ${response.status.value}")
        }
    }
}
