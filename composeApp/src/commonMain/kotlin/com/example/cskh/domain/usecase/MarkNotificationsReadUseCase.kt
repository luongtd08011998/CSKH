package com.example.cskh.domain.usecase

import com.example.cskh.domain.repository.NotificationRepository

class MarkNotificationsReadUseCase(
    private val repository: NotificationRepository,
) {
    suspend fun markRead(baseUrl: String, ids: List<Long>? = null, isSystem: Boolean? = null): Result<Unit> =
        repository.markRead(baseUrl, ids, isSystem)
}

