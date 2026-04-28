package com.example.cskh.domain.usecase

import com.example.cskh.domain.model.FeedbackDetail
import com.example.cskh.domain.repository.FeedbackRepository

class GetFeedbackDetailUseCase(
    private val repo: FeedbackRepository,
    private val preferences: UserFormPreferencesUseCase,
) {
    suspend operator fun invoke(id: Long): Result<FeedbackDetail> {
        val baseUrl = preferences.getBaseUrl()
        if (baseUrl.isBlank()) return Result.failure(IllegalStateException("Thiếu địa chỉ API. Vui lòng đăng nhập lại."))
        return repo.getFeedbackDetail(baseUrl, id)
    }
}
