package com.example.cskh.domain.model

data class NotificationItem(
    val id: Long,
    val customerId: Long?,
    val title: String,
    val content: String,
    val type: String,
    val isRead: Boolean,
    val createdAt: String,
    val referenceId: Long? = null,
    val isSystem: Boolean = false,
)

