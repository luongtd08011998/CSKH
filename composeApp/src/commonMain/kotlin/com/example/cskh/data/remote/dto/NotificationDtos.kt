package com.example.cskh.data.remote.dto

import com.example.cskh.domain.model.NotificationItem
import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val id: Long? = null,
    val customerId: Long? = null,
    val title: String? = null,
    val content: String? = null,
    val type: String? = null,
    val isRead: Boolean? = null,
    val createdAt: String? = null,
    val referenceId: Long? = null,
    val isSystem: Boolean? = null,
    val url: String? = null,
)

@Serializable
data class NotificationsResponseDto(
    val data: List<NotificationDto>? = null,
    val message: String? = null,
    val statusCode: Int? = null,
)

@Serializable
data class MarkReadRequestDto(
    val ids: List<Long>? = null,
    val isSystem: Boolean? = null,
)

fun NotificationDto.toDomain(): NotificationItem = NotificationItem(
    id = id ?: 0L,
    customerId = customerId,
    title = title.orEmpty(),
    content = content.orEmpty(),
    type = type.orEmpty(),
    isRead = isRead ?: false,
    createdAt = createdAt.orEmpty(),
    referenceId = referenceId,
    isSystem = isSystem ?: false,
    url = url,
)

