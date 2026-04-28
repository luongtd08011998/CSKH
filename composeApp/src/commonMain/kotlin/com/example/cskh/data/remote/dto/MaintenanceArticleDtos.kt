package com.example.cskh.data.remote.dto

import com.example.cskh.domain.model.MaintenanceArticle
import com.example.cskh.domain.model.PagedMaintenanceArticles
import kotlinx.serialization.Serializable

@Serializable
data class MaintenanceArticleDto(
    val id: Long? = null,
    val title: String? = null,
    val slug: String? = null,
    val content: String? = null,
    val thumbnail: String? = null,
    val views: Int? = null,
    val createdAt: String? = null,
)

@Serializable
data class MaintenancePageDto(
    val meta: MetaDto,
    val result: List<MaintenanceArticleDto>,
)

@Serializable
data class MaintenanceResponseDto(
    val data: MaintenancePageDto? = null,
    val message: String? = null,
    val statusCode: Int? = null,
)

fun MaintenanceArticleDto.toDomain(): MaintenanceArticle = MaintenanceArticle(
    id = id ?: 0L,
    title = title.orEmpty(),
    slug = slug.orEmpty(),
    content = content.orEmpty(),
    thumbnail = thumbnail,
    views = views ?: 0,
    createdAt = createdAt.orEmpty(),
)
