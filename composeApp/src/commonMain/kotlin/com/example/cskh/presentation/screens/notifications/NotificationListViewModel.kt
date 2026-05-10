package com.example.cskh.presentation.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cskh.data.session.TokenRefreshCoordinator
import com.example.cskh.domain.model.InvoiceSummary
import com.example.cskh.domain.model.MaintenanceArticle
import com.example.cskh.domain.model.NotificationItem
import com.example.cskh.domain.model.NotificationType
import com.example.cskh.domain.model.toNotificationType
import com.example.cskh.domain.model.PageMeta
import com.example.cskh.domain.usecase.GetMaintenanceArticlesUseCase
import com.example.cskh.domain.usecase.GetFeaturedArticlesUseCase
import com.example.cskh.domain.usecase.GetInvoicesUseCase
import com.example.cskh.domain.usecase.GetNotificationsUseCase
import com.example.cskh.domain.usecase.MarkNotificationsReadUseCase
import com.example.cskh.domain.usecase.UserFormPreferencesUseCase
import com.example.cskh.presentation.NotificationBadgeStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class NotificationListUiState(
    val items: List<NotificationItem> = emptyList(),
    val invoices: List<InvoiceSummary> = emptyList(),
    val isLoading: Boolean = false,
    val isMarkingRead: Boolean = false,
    val errorMessage: String? = null,
    val sessionExpired: Boolean = false,
)

data class MaintenanceUiState(
    val items: List<MaintenanceArticle> = emptyList(),
    val meta: PageMeta? = null,
    val currentPage: Int = 0,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
)

data class FeaturedUiState(
    val items: List<MaintenanceArticle> = emptyList(),
    val meta: PageMeta? = null,
    val currentPage: Int = 0,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
)

class NotificationListViewModel(
    private val getNotifications: GetNotificationsUseCase,
    private val markRead: MarkNotificationsReadUseCase,
    private val formPreferences: UserFormPreferencesUseCase,
    private val notificationBadgeStore: NotificationBadgeStore,
    private val getMaintenanceArticles: GetMaintenanceArticlesUseCase,
    private val getFeaturedArticles: GetFeaturedArticlesUseCase,
    private val tokenRefresh: TokenRefreshCoordinator,
    private val getInvoices: GetInvoicesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationListUiState())
    val state: StateFlow<NotificationListUiState> = _state.asStateFlow()

    private val _maintenanceState = MutableStateFlow(MaintenanceUiState())
    val maintenanceState: StateFlow<MaintenanceUiState> = _maintenanceState.asStateFlow()

    private val _featuredState = MutableStateFlow(FeaturedUiState())
    val featuredState: StateFlow<FeaturedUiState> = _featuredState.asStateFlow()

    init {
        refresh()
        refreshMaintenance()
        refreshFeatured()
    }

    fun refresh() {
        val baseUrl = formPreferences.getBaseUrl()
        if (baseUrl.isBlank()) {
            _state.update { it.copy(errorMessage = "Thiếu địa chỉ API. Vui lòng đăng nhập lại.") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val invoicesDeferred = async(Dispatchers.Default) { getInvoices(baseUrl, 1, 20) }
            val result = withContext(Dispatchers.Default) {
                getNotifications(baseUrl)
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
                    val invoices = invoicesDeferred.await().getOrNull()?.items.orEmpty()

                    _state.update { it.copy(items = items, invoices = invoices, isLoading = false) }
                    notificationBadgeStore.syncFromItems(items)
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Không tải được danh sách thông báo",
                        )
                    }
                },
            )
        }
    }

    fun refreshMaintenance() {
        val baseUrl = formPreferences.getBaseUrl()
        if (baseUrl.isBlank()) return
        viewModelScope.launch {
            _maintenanceState.update { it.copy(isLoading = true, errorMessage = null, currentPage = 0) }
            val result = withContext(Dispatchers.Default) { getMaintenanceArticles(baseUrl, page = 0, size = 10) }

            if (isUnauthorized(result)) {
                if (!tokenRefresh.tryRefresh()) {
                    _state.update { it.copy(sessionExpired = true) }
                    return@launch
                }
                refreshMaintenance()
                return@launch
            }

            result.fold(
                onSuccess = { paged ->
                    _maintenanceState.update {
                        it.copy(items = paged.items, meta = paged.meta, currentPage = 0, isLoading = false)
                    }
                },
                onFailure = { e ->
                    _maintenanceState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "Không tải được bài viết bảo trì")
                    }
                },
            )
        }
    }

    fun loadMoreMaintenance() {
        val ms = _maintenanceState.value
        val meta = ms.meta ?: return
        val nextPage = ms.currentPage + 1
        if (nextPage >= meta.pages || ms.isLoadingMore) return

        val baseUrl = formPreferences.getBaseUrl()
        if (baseUrl.isBlank()) return
        viewModelScope.launch {
            _maintenanceState.update { it.copy(isLoadingMore = true) }
            val result = withContext(Dispatchers.Default) { getMaintenanceArticles(baseUrl, page = nextPage, size = 10) }

            if (isUnauthorized(result)) {
                if (!tokenRefresh.tryRefresh()) {
                    _state.update { it.copy(sessionExpired = true) }
                    return@launch
                }
                loadMoreMaintenance()
                return@launch
            }

            result.fold(
                onSuccess = { paged ->
                    _maintenanceState.update {
                        it.copy(
                            items = it.items + paged.items,
                            meta = paged.meta,
                            currentPage = nextPage,
                            isLoadingMore = false,
                        )
                    }
                },
                onFailure = { e ->
                    _maintenanceState.update {
                        it.copy(isLoadingMore = false, errorMessage = e.message ?: "Không tải thêm được")
                    }
                },
            )
        }
    }

    fun refreshFeatured() {
        val baseUrl = formPreferences.getBaseUrl()
        if (baseUrl.isBlank()) return
        viewModelScope.launch {
            _featuredState.update { it.copy(isLoading = true, errorMessage = null, currentPage = 0) }
            val result = withContext(Dispatchers.Default) { getFeaturedArticles(baseUrl, page = 0, size = 10) }

            if (isUnauthorized(result)) {
                if (!tokenRefresh.tryRefresh()) {
                    _state.update { it.copy(sessionExpired = true) }
                    return@launch
                }
                refreshFeatured()
                return@launch
            }

            result.fold(
                onSuccess = { paged ->
                    _featuredState.update {
                        it.copy(items = paged.items, meta = paged.meta, currentPage = 0, isLoading = false)
                    }
                },
                onFailure = { e ->
                    _featuredState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "Không tải được bài viết nổi bật")
                    }
                },
            )
        }
    }

    fun loadMoreFeatured() {
        val fs = _featuredState.value
        val meta = fs.meta ?: return
        val nextPage = fs.currentPage + 1
        if (nextPage >= meta.pages || fs.isLoadingMore) return

        val baseUrl = formPreferences.getBaseUrl()
        if (baseUrl.isBlank()) return
        viewModelScope.launch {
            _featuredState.update { it.copy(isLoadingMore = true) }
            val result = withContext(Dispatchers.Default) { getFeaturedArticles(baseUrl, page = nextPage, size = 10) }

            if (isUnauthorized(result)) {
                if (!tokenRefresh.tryRefresh()) {
                    _state.update { it.copy(sessionExpired = true) }
                    return@launch
                }
                loadMoreFeatured()
                return@launch
            }

            result.fold(
                onSuccess = { paged ->
                    _featuredState.update {
                        it.copy(
                            items = it.items + paged.items,
                            meta = paged.meta,
                            currentPage = nextPage,
                            isLoadingMore = false,
                        )
                    }
                },
                onFailure = { e ->
                    _featuredState.update {
                        it.copy(isLoadingMore = false, errorMessage = e.message ?: "Không tải thêm được")
                    }
                },
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
        _maintenanceState.update { it.copy(errorMessage = null) }
        _featuredState.update { it.copy(errorMessage = null) }
    }

    fun acknowledgeSessionExpired() {
        _state.update { it.copy(sessionExpired = false) }
    }

    fun markAllRead() {
        val baseUrl = formPreferences.getBaseUrl()
        if (baseUrl.isBlank()) {
            _state.update { it.copy(errorMessage = "Thiếu địa chỉ API. Vui lòng đăng nhập lại.") }
            return
        }
        _state.update { st ->
            st.copy(items = st.items.map { it.copy(isRead = true) })
        }
        notificationBadgeStore.syncFromItems(_state.value.items)
        _state.update { it.copy(isMarkingRead = true, errorMessage = null) }
        viewModelScope.launch {
            val resultFalse = withContext(Dispatchers.Default) { markRead.markRead(baseUrl, ids = null, isSystem = false) }
            val resultTrue = withContext(Dispatchers.Default) { markRead.markRead(baseUrl, ids = null, isSystem = true) }
            
            // Note: If 401 here, we could also retry, but markAllRead is a fire-and-forget-ish action
            // for UI. Let's focus on main data loading for now.

            val failures = listOfNotNull(
                resultFalse.exceptionOrNull()?.message,
                resultTrue.exceptionOrNull()?.message,
            )
            if (failures.isNotEmpty()) {
                _state.update {
                    it.copy(
                        isMarkingRead = false,
                        errorMessage = failures.joinToString("; "),
                    )
                }
            } else {
                _state.update { it.copy(isMarkingRead = false) }
            }
        }
    }

    fun markRead(id: Long) {
        if (id <= 0) return
        val item = _state.value.items.find { it.id == id }
        _state.update { st ->
            st.copy(items = st.items.map { if (it.id == id) it.copy(isRead = true) else it })
        }
        notificationBadgeStore.syncFromItems(_state.value.items)
        markReadInternal(ids = listOf(id), isSystem = item?.isSystem)
    }

    private fun markReadInternal(ids: List<Long>?, isSystem: Boolean? = null) {
        val baseUrl = formPreferences.getBaseUrl()
        if (baseUrl.isBlank()) {
            _state.update { it.copy(errorMessage = "Thiếu địa chỉ API. Vui lòng đăng nhập lại.") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isMarkingRead = true, errorMessage = null) }
            val result = withContext(Dispatchers.Default) { markRead.markRead(baseUrl, ids, isSystem) }
            result.fold(
                onSuccess = {
                    if (ids == null) {
                        _state.update { st -> st.copy(items = st.items.map { it.copy(isRead = true) }) }
                    }
                    _state.update { it.copy(isMarkingRead = false) }
                    notificationBadgeStore.syncFromItems(_state.value.items)
                },
                onFailure = { e ->
                    if (isUnauthorized(result)) {
                        if (!tokenRefresh.tryRefresh()) {
                            _state.update { it.copy(isMarkingRead = false, sessionExpired = true) }
                            return@launch
                        }
                        markReadInternal(ids, isSystem)
                        return@launch
                    }
                    _state.update {
                        it.copy(
                            isMarkingRead = false,
                            errorMessage = e.message ?: "Không cập nhật trạng thái đã đọc",
                        )
                    }
                },
            )
        }
    }

    private fun isUnauthorized(result: Result<*>): Boolean =
        result.exceptionOrNull()?.message?.let {
            it.contains("401") || it.contains("UNAUTHORIZED_401") || it.contains("Chưa đăng nhập")
        } == true

    /**
     * Parse "tháng 03/2026" từ notification content → "2026-03" để match với invoice.yearMonth.
     */
    private fun parseMonthFromContent(content: String): String? {
        val match = Regex("tháng\\s+(\\d{2})/(\\d{4})", RegexOption.IGNORE_CASE).find(content)
            ?: return null
        val month = match.groupValues[1]
        val year = match.groupValues[2]
        return "$year-$month"
    }

    /**
     * Tìm đúng invoice ID dựa trên tháng từ notification content.
     * Fallback về referenceId nếu không tìm thấy.
     */
    fun resolveInvoiceId(notification: NotificationItem): Long? {
        if (notification.type.toNotificationType() != NotificationType.BILLING) {
            return notification.referenceId
        }
        val targetMonth = parseMonthFromContent(notification.content) ?: return notification.referenceId
        val invoices = _state.value.invoices

        // Ưu tiên tìm invoice có yearMonth khớp chính xác
        val matched = invoices.firstOrNull {
            it.yearMonth.startsWith(targetMonth)
        }
        return matched?.id ?: notification.referenceId
    }
}
