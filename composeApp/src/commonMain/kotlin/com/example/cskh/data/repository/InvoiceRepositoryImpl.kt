package com.example.cskh.data.repository

import com.example.cskh.data.remote.dto.InvoiceDetailResponseDto
import com.example.cskh.data.remote.dto.InvoicesListResponseDto
import com.example.cskh.data.remote.dto.toDomain
import com.example.cskh.data.session.SessionManager
import com.example.cskh.domain.model.InvoiceDetail
import com.example.cskh.domain.model.PagedInvoices
import com.example.cskh.domain.repository.InvoiceRepository
import com.example.cskh.platform.BinaryGetDownloader
import com.example.cskh.util.normalizeApiBaseUrl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders

class InvoiceRepositoryImpl(
    private val client: HttpClient,
    private val sessionManager: SessionManager,
    private val binaryDownloader: BinaryGetDownloader,
) : InvoiceRepository {

    override suspend fun getInvoices(baseUrl: String, page: Int, pageSize: Int): Result<PagedInvoices> =
        runCatching {
            val token = sessionManager.accessToken ?: error("Chưa đăng nhập")
            val url = "${normalizeApiBaseUrl(baseUrl)}/api/v1/qlkh/invoices"
            val response = client.get(url) {
                header(HttpHeaders.Authorization, "Bearer $token")
                parameter("page", page)
                parameter("pageSize", pageSize)
            }
            if (response.status.value !in 200..299) {
                val text = runCatching { response.bodyAsText() }.getOrNull()
                error(text ?: "HTTP ${response.status.value}")
            }
            val envelope = response.body<InvoicesListResponseDto>()
            val data = envelope.data ?: error(envelope.message ?: "Không có dữ liệu")
            data.toDomain()
        }

    override suspend fun getInvoiceDetail(baseUrl: String, id: Long): Result<InvoiceDetail> = runCatching {
        val token = sessionManager.accessToken ?: error("Chưa đăng nhập")
        val url = "${normalizeApiBaseUrl(baseUrl)}/api/v1/qlkh/invoices/$id"
        val response = client.get(url) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        if (response.status.value !in 200..299) {
            val text = runCatching { response.bodyAsText() }.getOrNull()
            error(text ?: "HTTP ${response.status.value}")
        }
        val envelope = response.body<InvoiceDetailResponseDto>()
        val dto = envelope.data ?: error(envelope.message ?: "Không có dữ liệu")
        dto.toDomain()
    }

    override suspend fun downloadEInvoiceZip(baseUrl: String, id: Long): Result<ByteArray> = runCatching {
        val token = sessionManager.accessToken ?: error("Chưa đăng nhập")
        val url = "${normalizeApiBaseUrl(baseUrl)}/api/v1/qlkh/invoices/$id/e-invoice-download"
        try {
            binaryDownloader.getBytes(url, token)
        } catch (e: Exception) {
            val m = e.message.orEmpty()
            if (m.contains("expected", ignoreCase = true) && m.contains("bytes but got", ignoreCase = true)) {
                error(
                    "Tải xuống không hoàn tất: dữ liệu nhận được ngắn hơn kích thước máy chủ khai báo (Content-Length). " +
                        "Hãy thử lại; nếu vẫn lỗi cần chỉnh API/proxy.",
                )
            }
            throw e
        }
    }
}
