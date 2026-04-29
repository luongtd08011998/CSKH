package com.example.cskh.domain.repository

import com.example.cskh.domain.model.PagedMaintenanceArticles

interface FeaturedArticleRepository {
    suspend fun getArticles(baseUrl: String, page: Int, size: Int): Result<PagedMaintenanceArticles>
}
