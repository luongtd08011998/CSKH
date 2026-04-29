package com.example.cskh.data.repository

import com.example.cskh.data.remote.dto.MaintenanceResponseDto
import com.example.cskh.data.remote.dto.toDomain
import com.example.cskh.domain.model.PagedMaintenanceArticles
import com.example.cskh.domain.repository.FeaturedArticleRepository
import com.example.cskh.util.normalizeApiBaseUrl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

class FeaturedArticleRepositoryImpl(
    private val client: HttpClient,
) : FeaturedArticleRepository {

    override suspend fun getArticles(baseUrl: String, page: Int, size: Int): Result<PagedMaintenanceArticles> = runCatching {
        val url = "${normalizeApiBaseUrl(baseUrl)}/api/v1/qlkh/customer/articles/featured?page=$page&size=$size"
        val response = client.get(url)
        if (response.status.value !in 200..299) {
            val text = runCatching { response.bodyAsText() }.getOrNull()
            error(text ?: "HTTP ${response.status.value}")
        }
        val envelope = response.body<MaintenanceResponseDto>()
        val pageData = envelope.data ?: error("Không có dữ liệu")
        PagedMaintenanceArticles(
            meta = pageData.meta.toDomain(),
            items = pageData.result.map { it.toDomain() },
        )
    }
}
