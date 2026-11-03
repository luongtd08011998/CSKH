package com.example.cskh.data.repository

import com.example.cskh.data.remote.dto.RegisterRequestDto
import com.example.cskh.data.remote.dto.RegisterResponseDto
import com.example.cskh.domain.repository.RegisterRepository
import com.example.cskh.platform.createTrustAllEngine
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class RegisterRepositoryImpl : RegisterRepository {

    companion object {
        private const val BASE_URL = "https://pay.toctienltd.vn"
        private const val AUTH_HEADER = "Basic bHVvbmd0ZEB0b2N0aWVubHRkLnZuOjEyMzEyMw=="
    }

    private val client by lazy {
        HttpClient(createTrustAllEngine()) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    override suspend fun submitRegistration(
        name: String,
        phone: String,
        waterMeterAddress: String,
        email: String,
    ): Result<String> = runCatching {
        val url = "$BASE_URL/cm-portlet/api/register_add"
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, AUTH_HEADER)
            setBody(RegisterRequestDto(name, phone, waterMeterAddress, email))
        }
        if (response.status.value !in 200..299) {
            error("HTTP ${response.status.value}")
        }
        val body = response.body<RegisterResponseDto>()
        if (body.retCode == "ERR_OK") {
            body.retMsg ?: "Đăng ký thành công"
        } else {
            error(body.retMsg ?: "Đăng ký thất bại")
        }
    }

    override suspend fun getRegistrationTime(phone: String): Result<String> = runCatching {
        val url = "$BASE_URL/cm-portlet/api/register_get/$phone"
        val response = client.get(url) {
            header(HttpHeaders.Authorization, AUTH_HEADER)
        }
        if (response.status.value !in 200..299) {
            error("HTTP ${response.status.value}")
        }
        val text = response.bodyAsText().trim().removeSurrounding("\"")
        if (text.isNotBlank() && !text.startsWith("<!DOCTYPE", ignoreCase = true) && !text.startsWith("<html", ignoreCase = true)) {
            text
        } else {
            error("Dữ liệu chưa được lưu vào CSDL (API register_get/$phone trả về rỗng).")
        }
    }
}
