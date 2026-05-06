package com.example.cskh.domain.usecase

import com.example.cskh.domain.repository.NotificationRepository

class BackfillReferenceIdUseCase(
    private val repository: NotificationRepository,
) {
    suspend operator fun invoke(baseUrl: String): Result<Unit> =
        repository.backfillReferenceId(baseUrl)
}
