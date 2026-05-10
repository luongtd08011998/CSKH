package com.example.cskh.presentation.screens.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cskh.data.session.TokenRefreshCoordinator
import com.example.cskh.domain.model.NotificationItem
import com.example.cskh.domain.usecase.GetNotificationsUseCase
import com.example.cskh.domain.usecase.MarkNotificationsReadUseCase
import com.example.cskh.domain.usecase.UserFormPreferencesUseCase
import com.example.cskh.presentation.NotificationBadgeStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class FeedbackNotificationUiState(
    val items: List<NotificationItem> = emptyList(),
    val isLoading: Boolean = false,
    val isMarkingRead: Boolean = false,
    val errorMessage: String? = null,
    val sessionExpired: Boolean = false,
)

class FeedbackNotificationViewModel(
    private val getNotifications: GetNotificationsUseCase,
    private val markRead: MarkNotificationsReadUseCase,
    private val formPreferences: UserFormPreferencesUseCase,
    private val notificationBadgeStore: NotificationBadgeStore,
    private val tokenRefresh: TokenRefreshCoordinator,
) : ViewModel() {

    private val _state = MutableStateFlow(FeedbackNotificationUiState())
    val state: StateFlow<FeedbackNotificationUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val baseUrl = formPreferences.getBaseUrl()
        if (baseUrl.isBlank()) {
            _state.update { it.copy(errorMessage = "Thiếu địa chỉ API. Vui lòng đăng nhập lại.") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            // Gọi API với ?type=FEEDBACK để chỉ lấy thông báo phản ánh
            val result = withContext(Dispatchers.Default) {
                getNotifications(baseUrl, type = "FEEDBACK")
            }

            if (isUnauthorized(result)) {
                if (!tokenRefresh.tryRefresh()) {
                    _state.update { it.copy(isLoading = false, sessionExpired = true) }
                    return@launch
                }
                refresh()
                return@launch
            }

            result.fold(
                onSuccess = { items ->
                    _state.update { it.copy(items = items, isLoading = false) }
                    // Cập nhật badge store sau khi đọc xong
                    notificationBadgeStore.refreshFromNetwork()
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Không tải được danh sách thông báo phản ánh",
                        )
                    }
                },
            )
        }
    }

    fun markItemRead(id: Long) {
        if (id <= 0) return
        val item = _state.value.items.find { it.id == id }
        // Cập nhật UI lạc quan ngay lập tức
        _state.update { st ->
            st.copy(items = st.items.map { if (it.id == id) it.copy(isRead = true) else it })
        }
        val baseUrl = formPreferences.getBaseUrl()
        if (baseUrl.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isMarkingRead = true) }
            markRead.markRead(baseUrl, ids = listOf(id), isSystem = item?.isSystem)
            _state.update { it.copy(isMarkingRead = false) }
            // Cập nhật lại badge sau khi đọc
            notificationBadgeStore.refreshFromNetwork()
        }
    }

    fun markAllRead() {
        val baseUrl = formPreferences.getBaseUrl()
        if (baseUrl.isBlank()) return
        // Cập nhật UI lạc quan
        _state.update { st ->
            st.copy(items = st.items.map { it.copy(isRead = true) })
        }
        viewModelScope.launch {
            _state.update { it.copy(isMarkingRead = true) }
            markRead.markRead(baseUrl, ids = null, isSystem = false)
            _state.update { it.copy(isMarkingRead = false) }
            notificationBadgeStore.refreshFromNetwork()
        }
    }

    fun acknowledgeSessionExpired() {
        _state.update { it.copy(sessionExpired = false) }
    }

    private fun isUnauthorized(result: Result<*>): Boolean =
        result.exceptionOrNull()?.message?.let {
            it.contains("401") || it.contains("UNAUTHORIZED_401") || it.contains("Chưa đăng nhập")
        } == true
}
