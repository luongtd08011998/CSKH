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
    val url: String? = null,
)

enum class NotificationType {
    BILLING,      // Hóa đơn
    MAINTENANCE,  // Cúp nước/Bảo trì
    GENERAL,      // Tin nổi bật
    FEEDBACK,     // Phản ánh dịch vụ
}

fun String.toNotificationType(): NotificationType {
    return when (this.uppercase()) {
        "BILLING", "INVOICE", "PAYMENT", "DEBT_REMINDER" -> NotificationType.BILLING
        "MAINTENANCE", "WATER_CUT" -> NotificationType.MAINTENANCE
        "FEEDBACK" -> NotificationType.FEEDBACK
        else -> NotificationType.GENERAL
    }
}


