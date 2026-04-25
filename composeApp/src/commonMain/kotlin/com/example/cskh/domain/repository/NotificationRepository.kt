package com.example.cskh.domain.repository

import com.example.cskh.domain.model.NotificationItem

interface NotificationRepository {
    suspend fun getNotifications(baseUrl: String): Result<List<NotificationItem>>
    suspend fun markRead(baseUrl: String, ids: List<Long>? = null): Result<Unit>
}

