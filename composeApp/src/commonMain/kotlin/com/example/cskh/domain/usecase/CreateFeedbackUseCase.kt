package com.example.cskh.domain.usecase

import com.example.cskh.domain.repository.FeedbackRepository
import com.example.cskh.platform.PickedImage

class CreateFeedbackUseCase(
    private val repo: FeedbackRepository,
    private val preferences: UserFormPreferencesUseCase,
) {
    suspend operator fun invoke(
        issueType: String,
        location: String,
        description: String,
        images: List<PickedImage>,
    ): Result<String> {
        val baseUrl = preferences.getBaseUrl()
        if (baseUrl.isBlank()) return Result.failure(IllegalStateException("Thiếu địa chỉ API. Vui lòng đăng nhập lại."))
        return repo.createFeedback(
            baseUrl = baseUrl,
            issueType = issueType,
            location = location,
            description = description,
            images = images,
        )
    }
}

