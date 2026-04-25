package com.example.cskh.domain.usecase

import com.example.cskh.domain.model.FeedbackItem
import com.example.cskh.domain.repository.FeedbackRepository

class GetFeedbacksUseCase(
    private val repo: FeedbackRepository,
    private val preferences: UserFormPreferencesUseCase,
) {
    suspend operator fun invoke(): Result<List<FeedbackItem>> {
        val baseUrl = preferences.getBaseUrl()
        if (baseUrl.isBlank()) return Result.failure(IllegalStateException("Thiếu địa chỉ API. Vui lòng đăng nhập lại."))
        return repo.getFeedbacks(baseUrl)
    }
}

