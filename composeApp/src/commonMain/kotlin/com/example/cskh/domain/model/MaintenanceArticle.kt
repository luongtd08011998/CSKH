package com.example.cskh.domain.model

data class MaintenanceArticle(
    val id: Long,
    val title: String,
    val slug: String,
    val content: String,
    val thumbnail: String?,
    val views: Int,
    val createdAt: String,
)

data class PagedMaintenanceArticles(
    val meta: PageMeta,
    val items: List<MaintenanceArticle>,
)
