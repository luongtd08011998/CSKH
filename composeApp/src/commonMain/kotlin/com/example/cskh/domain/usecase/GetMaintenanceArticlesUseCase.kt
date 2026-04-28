package com.example.cskh.domain.usecase

import com.example.cskh.domain.model.PagedMaintenanceArticles
import com.example.cskh.domain.repository.MaintenanceArticleRepository

class GetMaintenanceArticlesUseCase(
    private val repository: MaintenanceArticleRepository,
) {
    suspend operator fun invoke(baseUrl: String, page: Int, size: Int): Result<PagedMaintenanceArticles> =
        repository.getArticles(baseUrl, page, size)
}
