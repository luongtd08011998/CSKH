package com.example.cskh.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cskh.data.session.TokenRefreshCoordinator
import com.example.cskh.domain.model.CustomerProfile
import com.example.cskh.domain.model.InvoiceDetail
import com.example.cskh.domain.model.InvoiceDisplayType
import com.example.cskh.domain.model.InvoiceSummary
import com.example.cskh.domain.model.ProcessedInvoice
import com.example.cskh.domain.usecase.GetCustomerMeUseCase
import com.example.cskh.domain.usecase.GetInvoiceDetailUseCase
import com.example.cskh.domain.usecase.GetInvoicesUseCase
import com.example.cskh.domain.usecase.UserFormPreferencesUseCase
import com.example.cskh.platform.defaultDevMachineApiBaseUrl
import com.example.cskh.presentation.CompanyBranding
import com.example.cskh.presentation.NotificationBadgeStore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val customer: CustomerProfile? = null,
    val currentInvoiceDetail: InvoiceDetail? = null,
    val recentInvoices: List<InvoiceSummary> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    /** true khi refresh token hết hạn → caller điều hướng về màn hình Login */
    val sessionExpired: Boolean = false,
    /** true khi request vẫn chưa xong sau SLOW_CONNECTION_THRESHOLD_MS */
    val isSlowConnection: Boolean = false,
)

class HomeViewModel(
    private val tokenRefresh: TokenRefreshCoordinator,
    private val formPreferences: UserFormPreferencesUseCase,
    private val getCustomerMe: GetCustomerMeUseCase,
    private val getInvoices: GetInvoicesUseCase,
    private val getInvoiceDetail: GetInvoiceDetailUseCase,
    private val notificationBadgeStore: NotificationBadgeStore,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    /** Job theo dõi slow-connection, cancel khi data về kịp */
    private var slowConnectionJob: Job? = null

    companion object {
        /** Ngưỡng thời gian (ms) trước khi hiện cảnh báo kết nối chậm */
        private const val SLOW_CONNECTION_THRESHOLD_MS = 5_000L
    }

    init {
        refresh()
    }

    /**
     * Đăng xuất: gọi API /auth/logout → xóa local session.
     * Caller (App.kt) lắng nghe sessionExpired hoặc onLogout callback để navigate.
     */
    fun logout(onLoggedOut: () -> Unit) {
        val baseUrl = defaultDevMachineApiBaseUrl(CompanyBranding.DEV_API_PORT)
        viewModelScope.launch {
            notificationBadgeStore.clear()
            tokenRefresh.logout(baseUrl)
            onLoggedOut()
        }
    }

    fun refresh() {
        val baseUrl = formPreferences.getBaseUrl()
        if (baseUrl.isBlank()) {
            _state.update { it.copy(errorMessage = "Thiếu địa chỉ API. Vui lòng đăng nhập lại.") }
            return
        }

        // Bắt đầu đếm thời gian kết nối chậm
        slowConnectionJob?.cancel()
        slowConnectionJob = viewModelScope.launch {
            delay(SLOW_CONNECTION_THRESHOLD_MS)
            _state.update { it.copy(isSlowConnection = true) }
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val meResult = getCustomerMe(baseUrl)
            // Nếu 401 → thử refresh token
            if (isUnauthorized(meResult)) {
                slowConnectionJob?.cancel()
                if (!tokenRefresh.tryRefresh()) {
                    _state.update { it.copy(isLoading = false, sessionExpired = true, isSlowConnection = false) }
                    return@launch
                }
                // Retry sau khi refresh thành công
                refresh()
                return@launch
            }

            val invoicesResult = getInvoices(baseUrl, 1, 5)
            val me = meResult.getOrNull()
            val rawInvoices = invoicesResult.getOrNull()?.items.orEmpty()
            val processed = processInvoices(rawInvoices)

            // Ưu tiên hiển thị hóa đơn Normal, nếu không có thì dùng Replacement, bỏ qua Replaced
            val displayable = processed.filter { it.displayType != InvoiceDisplayType.Replaced }
            val newest = displayable.firstOrNull()
            val newestId = newest?.invoice?.id
            val detailResult = if (newestId != null) getInvoiceDetail(baseUrl, newestId) else Result.success(null)
            val detail = detailResult.getOrNull()

            // Data đã về → hủy timeout slow-connection
            slowConnectionJob?.cancel()

            _state.update {
                it.copy(
                    customer = me,
                    currentInvoiceDetail = detail,
                    recentInvoices = displayable.map { p -> p.invoice },
                    isLoading = false,
                    isSlowConnection = false,
                    errorMessage = (meResult.exceptionOrNull()
                        ?: invoicesResult.exceptionOrNull()
                        ?: detailResult.exceptionOrNull())?.message,
                )
            }
            notificationBadgeStore.refreshFromNetwork()
        }
    }

    /** Được gọi từ nút "Thử lại" – reset slow-connection flag rồi load lại */
    fun retry() {
        _state.update { it.copy(isSlowConnection = false, isLoading = true, errorMessage = null) }
        refresh()
    }

    fun acknowledgeSessionExpired() {
        _state.update { it.copy(sessionExpired = false) }
    }

    private fun isUnauthorized(result: Result<*>): Boolean =
        result.exceptionOrNull()?.message?.let {
            it.contains("401") || it.contains("UNAUTHORIZED_401") || it.contains("Chưa đăng nhập")
        } == true

    private fun processInvoices(items: List<InvoiceSummary>): List<ProcessedInvoice> {
        val grouped = items.groupBy { it.yearMonth }
        val processed = mutableListOf<ProcessedInvoice>()
        for ((_, group) in grouped) {
            val zeroInvoice = group.firstOrNull { it.totalAmount == 0.0 }
            if (group.size > 1 && zeroInvoice != null) {
                processed.add(ProcessedInvoice(zeroInvoice, InvoiceDisplayType.Replacement))
                group.filter { it.id != zeroInvoice.id }.forEach {
                    processed.add(ProcessedInvoice(it, InvoiceDisplayType.Replaced))
                }
            } else {
                group.forEach { processed.add(ProcessedInvoice(it, InvoiceDisplayType.Normal)) }
            }
        }
        return processed.sortedWith(
            compareByDescending<ProcessedInvoice> { it.invoice.yearMonth }
                .thenByDescending { it.invoice.id }
        )
    }
}
