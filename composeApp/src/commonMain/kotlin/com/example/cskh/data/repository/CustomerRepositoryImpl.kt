package com.example.cskh.data.repository

import com.example.cskh.data.remote.dto.CustomerMeResponseDto
import com.example.cskh.data.remote.dto.DocumentDto
import com.example.cskh.data.remote.dto.toDomain
import com.example.cskh.data.session.SessionManager
import com.example.cskh.domain.model.CustomerDocument
import com.example.cskh.domain.model.CustomerProfile
import com.example.cskh.domain.repository.CustomerRepository
import com.example.cskh.util.normalizeApiBaseUrl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
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

    override suspend fun getDocumentList(code: String, phone: String): Result<List<CustomerDocument>> = runCatching {
        val url = "https://toctienltd.vn/cm-portlet/api/cust_doc_list?code=$code&phone=$phone"
        val response = client.get(url)
        if (response.status.value !in 200..299) {
            val text = runCatching { response.bodyAsText() }.getOrNull()
            error(text ?: "HTTP ${response.status.value}")
        }
        val dtos = response.body<List<DocumentDto>>()
        dtos.map { it.toDomain() }
    }

    override suspend fun downloadDocument(fullname: String): Result<ByteArray> = runCatching {
        val response = client.get("https://toctienltd.vn/cm-portlet/api/cust_doc") {
            parameter("f", fullname)
        }
        if (response.status.value !in 200..299) {
            val text = runCatching { response.bodyAsText() }.getOrNull()
            error(text?.takeIf { it.isNotBlank() } ?: "HTTP ${response.status.value}")
        }
        val bytes = response.body<ByteArray>()
        if (bytes.isEmpty()) error("Dữ liệu tệp trống")
        bytes
    }
}
