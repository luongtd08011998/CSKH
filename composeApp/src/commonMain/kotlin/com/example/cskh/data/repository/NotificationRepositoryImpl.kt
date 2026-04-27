package com.example.cskh.data.repository

import com.example.cskh.data.remote.dto.MarkReadRequestDto
import com.example.cskh.data.remote.dto.NotificationsResponseDto
import com.example.cskh.data.remote.dto.toDomain
import com.example.cskh.data.session.SessionManager
import com.example.cskh.domain.model.NotificationItem
import com.example.cskh.domain.repository.NotificationRepository
import com.example.cskh.util.normalizeApiBaseUrl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class NotificationRepositoryImpl(
    private val client: HttpClient,
    private val sessionManager: SessionManager,
) : NotificationRepository {

    override suspend fun getNotifications(baseUrl: String): Result<List<NotificationItem>> = runCatching {
        val token = sessionManager.accessToken ?: error("Chưa đăng nhập")
        val url = "${normalizeApiBaseUrl(baseUrl)}/api/v1/qlkh/customer/notifications"
        val response = client.get(url) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        if (response.status.value !in 200..299) {
            val text = runCatching { response.bodyAsText() }.getOrNull()
            error(text ?: "HTTP ${response.status.value}")
        }
        val envelope = response.body<NotificationsResponseDto>()
        (envelope.data ?: emptyList()).map { it.toDomain() }
    }

    override suspend fun markRead(baseUrl: String, ids: List<Long>?, isSystem: Boolean?): Result<Unit> = runCatching {
        val token = sessionManager.accessToken ?: error("Chưa đăng nhập")
        val url = "${normalizeApiBaseUrl(baseUrl)}/api/v1/qlkh/customer/notifications/read"
        val response = client.post(url) {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(MarkReadRequestDto(ids = ids, isSystem = isSystem))
        }
        if (response.status.value !in 200..299) {
            val text = runCatching { response.bodyAsText() }.getOrNull()
            error(text ?: "HTTP ${response.status.value}")
        }
    }
}

