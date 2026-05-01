package com.example.cskh.presentation.screens.invoices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cskh.data.session.TokenRefreshCoordinator
import com.example.cskh.domain.model.InvoiceSummary
import com.example.cskh.domain.model.PageMeta
import com.example.cskh.domain.usecase.GetInvoicesUseCase
import com.example.cskh.domain.usecase.UserFormPreferencesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class InvoicePaymentFilter {
    All,
    Paid,
    Unpaid,
}

data class InvoiceListUiState(
    val items: List<InvoiceSummary> = emptyList(),
    val meta: PageMeta? = null,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val currentPage: Int = 1,
    val searchQuery: String = "",
    val paymentFilter: InvoicePaymentFilter = InvoicePaymentFilter.All,
    /** true khi refresh token hết hạn → caller điều hướng về màn hình Login */
    val sessionExpired: Boolean = false,
)

class InvoiceListViewModel(
    private val getInvoices: GetInvoicesUseCase,
    private val formPreferences: UserFormPreferencesUseCase,
    private val tokenRefresh: TokenRefreshCoordinator,
) : ViewModel() {

    private val _state = MutableStateFlow(InvoiceListUiState())
    val state: StateFlow<InvoiceListUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun setSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun setPaymentFilter(filter: InvoicePaymentFilter) {
        _state.update { it.copy(paymentFilter = filter) }
    }

    fun refresh() {
        loadPage(1, append = false)
    }

    fun loadMore() {
        val s = _state.value
        val meta = s.meta ?: return
        if (s.isLoading || s.isLoadingMore) return
        if (s.currentPage >= meta.pages) return
        loadPage(s.currentPage + 1, append = true)
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun acknowledgeSessionExpired() {
        _state.update { it.copy(sessionExpired = false) }
    }

    private fun loadPage(page: Int, append: Boolean) {
        val baseUrl = formPreferences.getBaseUrl()
        if (baseUrl.isBlank()) {
            _state.update { it.copy(errorMessage = "Thiếu địa chỉ API. Vui lòng đăng nhập lại.") }
            return
        }
        viewModelScope.launch {
            if (append) {
                _state.update { it.copy(isLoadingMore = true, errorMessage = null) }
            } else {
                _state.update { it.copy(isLoading = true, errorMessage = null) }
            }
            val result = getInvoices(baseUrl, page, PAGE_SIZE)

            // Xử lý Unauthorized (401)
            if (isUnauthorized(result)) {
                if (!tokenRefresh.tryRefresh()) {
                    _state.update { it.copy(isLoading = false, isLoadingMore = false, sessionExpired = true) }
                    return@launch
                }
                // Thử lại sau khi refresh thành công
                loadPage(page, append)
                return@launch
            }

            result.fold(
                onSuccess = { paged ->
                    _state.update { st ->
                        val merged = if (append) st.items + paged.items else paged.items
                        st.copy(
                            items = merged,
                            meta = paged.meta,
                            currentPage = page,
                            isLoading = false,
                            isLoadingMore = false,
                        )
                    }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            errorMessage = e.message ?: "Không tải được danh sách",
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

    companion object {
        private const val PAGE_SIZE = 20
    }
}
