package com.example.cskh.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cskh.data.session.SessionManager
import com.example.cskh.domain.model.CustomerProfile
import com.example.cskh.domain.model.InvoiceDetail
import com.example.cskh.domain.model.InvoiceSummary
import com.example.cskh.domain.usecase.GetCustomerMeUseCase
import com.example.cskh.domain.usecase.GetInvoiceDetailUseCase
import com.example.cskh.domain.usecase.GetInvoicesUseCase
import com.example.cskh.domain.usecase.UserFormPreferencesUseCase
import com.example.cskh.platform.FcmDeviceSync
import com.example.cskh.presentation.NotificationBadgeStore
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
)

class HomeViewModel(
    private val sessionManager: SessionManager,
    private val formPreferences: UserFormPreferencesUseCase,
    private val getCustomerMe: GetCustomerMeUseCase,
    private val getInvoices: GetInvoicesUseCase,
    private val getInvoiceDetail: GetInvoiceDetailUseCase,
    private val notificationBadgeStore: NotificationBadgeStore,
    private val fcmDeviceSync: FcmDeviceSync,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun logout() {
        viewModelScope.launch {
            runCatching { fcmDeviceSync.unregisterIfLoggedIn() }
            notificationBadgeStore.clear()
            formPreferences.clearAccessToken()
            sessionManager.clear()
        }
    }

    fun refresh() {
        val baseUrl = formPreferences.getBaseUrl()
        if (baseUrl.isBlank()) {
            _state.update { it.copy(errorMessage = "Thiếu địa chỉ API. Vui lòng đăng nhập lại.") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val meResult = getCustomerMe(baseUrl)
            val invoicesResult = getInvoices(baseUrl, 1, 5)

            val me = meResult.getOrNull()
            val invoices = invoicesResult.getOrNull()?.items.orEmpty()
            val newestId = invoices.firstOrNull()?.id
            val detailResult = if (newestId != null) getInvoiceDetail(baseUrl, newestId) else Result.success(null)
            val detail = detailResult.getOrNull()

            _state.update {
                it.copy(
                    customer = me,
                    currentInvoiceDetail = detail,
                    recentInvoices = invoices,
                    isLoading = false,
                    errorMessage = (meResult.exceptionOrNull() ?: invoicesResult.exceptionOrNull() ?: detailResult.exceptionOrNull())?.message,
                )
            }
            notificationBadgeStore.refreshFromNetwork()
        }
    }
}
