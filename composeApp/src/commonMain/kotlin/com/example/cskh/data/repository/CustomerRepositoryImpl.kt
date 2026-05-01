package com.example.cskh.data.repository

import com.example.cskh.data.remote.dto.CustomerMeResponseDto
import com.example.cskh.data.remote.dto.toDomain
import com.example.cskh.data.session.SessionManager
import com.example.cskh.domain.model.CustomerProfile
import com.example.cskh.domain.repository.CustomerRepository
import com.example.cskh.util.normalizeApiBaseUrl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders

class CustomerRepositoryImpl(
    private val client: HttpClient,
    private val sessionManager: SessionManager,
) : CustomerRepository {

    override suspend fun getMe(baseUrl: String): Result<CustomerProfile> = runCatching {
        val token = sessionManager.accessToken ?: error("Chưa đăng nhập")
        val url = "${normalizeApiBaseUrl(baseUrl)}/api/v1/qlkh/customers/me"
        val response = client.get(url) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        if (response.status.value == 401) {
            error("UNAUTHORIZED_401")
        }
        if (response.status.value !in 200..299) {
            val text = runCatching { response.bodyAsText() }.getOrNull()
            error(text ?: "HTTP ${response.status.value}")
        }
        val envelope = response.body<CustomerMeResponseDto>()
        val dto = envelope.data ?: error(envelope.message ?: "Không có dữ liệu khách hàng")
        dto.toDomain()
    }
}
