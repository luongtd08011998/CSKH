package com.example.cskh.data.repository

import com.example.cskh.data.remote.dto.LoginRequestDto
import com.example.cskh.data.remote.dto.LoginResponseDto
import com.example.cskh.domain.repository.AuthRepository
import com.example.cskh.util.normalizeApiBaseUrl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthRepositoryImpl(
    private val client: HttpClient,
) : AuthRepository {

    override suspend fun login(baseUrl: String, digiCode: String, phone: String): Result<String> = runCatching {
        val url = "${normalizeApiBaseUrl(baseUrl)}/api/v1/qlkh/auth/login"
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(LoginRequestDto(digiCode = digiCode, phone = phone))
        }
        if (response.status.value !in 200..299) {
            val text = runCatching { response.bodyAsText() }.getOrNull()
            error(text ?: "HTTP ${response.status.value}")
        }
        val envelope = response.body<LoginResponseDto>()
        envelope.data?.accessToken ?: error(envelope.message ?: "Không nhận được accessToken")
    }
}
