package com.example.cskh.domain.repository

import com.example.cskh.domain.model.NotificationItem

interface NotificationRepository {
    suspend fun getNotifications(baseUrl: String, type: String? = null, excludeType: String? = null): Result<List<NotificationItem>>
    suspend fun markRead(baseUrl: String, ids: List<Long>? = null, isSystem: Boolean? = null): Result<Unit>
    suspend fun backfillReferenceId(baseUrl: String): Result<Unit>
}

