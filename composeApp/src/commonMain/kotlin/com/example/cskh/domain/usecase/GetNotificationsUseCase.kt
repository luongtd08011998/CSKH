package com.example.cskh.domain.usecase

import com.example.cskh.domain.model.NotificationItem
import com.example.cskh.domain.repository.NotificationRepository

class GetNotificationsUseCase(
    private val repository: NotificationRepository,
) {
    suspend operator fun invoke(baseUrl: String): Result<List<NotificationItem>> =
        repository.getNotifications(baseUrl)
}

