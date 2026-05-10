package com.example.cskh.domain.usecase

import com.example.cskh.domain.model.NotificationItem
import com.example.cskh.domain.repository.NotificationRepository

class GetNotificationsUseCase(
    private val repository: NotificationRepository,
) {
    suspend operator fun invoke(
        baseUrl: String,
        type: String? = null,
        excludeType: String? = null,
    ): Result<List<NotificationItem>> =
        repository.getNotifications(baseUrl, type = type, excludeType = excludeType)
}

