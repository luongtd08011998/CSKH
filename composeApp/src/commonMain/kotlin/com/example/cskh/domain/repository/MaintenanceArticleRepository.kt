package com.example.cskh.domain.repository

import com.example.cskh.domain.model.PagedMaintenanceArticles

interface MaintenanceArticleRepository {
    suspend fun getArticles(baseUrl: String, page: Int, size: Int): Result<PagedMaintenanceArticles>
}
