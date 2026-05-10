package com.example.cskh.presentation

import com.example.cskh.domain.model.NotificationItem
import com.example.cskh.domain.model.NotificationType
import com.example.cskh.domain.model.toNotificationType
import com.example.cskh.domain.usecase.GetNotificationsUseCase
import com.example.cskh.domain.usecase.UserFormPreferencesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationBadgeStore(
    private val getNotifications: GetNotificationsUseCase,
    private val formPreferences: UserFormPreferencesUseCase,
) {

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    private val _feedbackUnreadCount = MutableStateFlow(0)
    val feedbackUnreadCount: StateFlow<Int> = _feedbackUnreadCount.asStateFlow()

    fun syncFromItems(items: List<NotificationItem>) {
        _unreadCount.value = items.count { !it.isRead && it.type.toNotificationType() != NotificationType.FEEDBACK }
    }

    fun clear() {
        _unreadCount.value = 0
        _feedbackUnreadCount.value = 0
    }

    suspend fun refreshFromNetwork() {
        val baseUrl = formPreferences.getBaseUrl()
        if (baseUrl.isBlank()) {
            _unreadCount.value = 0
            _feedbackUnreadCount.value = 0
            return
        }
        // Dùng excludeType=FEEDBACK để badge thông báo chung không tính phản ánh
        getNotifications(baseUrl, excludeType = "FEEDBACK").onSuccess { syncFromItems(it) }
        // Fetch riêng badge phản ánh
        getNotifications(baseUrl, type = "FEEDBACK").onSuccess { items ->
            _feedbackUnreadCount.value = items.count { !it.isRead }
        }
    }
}
