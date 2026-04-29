package com.example.cskh.domain.usecase

import com.example.cskh.domain.model.PagedMaintenanceArticles
import com.example.cskh.domain.repository.FeaturedArticleRepository

class GetFeaturedArticlesUseCase(
    private val repository: FeaturedArticleRepository,
) {
    suspend operator fun invoke(baseUrl: String, page: Int, size: Int): Result<PagedMaintenanceArticles> =
        repository.getArticles(baseUrl, page, size)
}
