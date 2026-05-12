package com.example.cskh.presentation.screens.invoices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cskh.data.session.TokenRefreshCoordinator
import com.example.cskh.domain.model.EInvoiceData
import com.example.cskh.domain.model.InvoiceDetail
import com.example.cskh.domain.usecase.DownloadAndSaveEInvoiceZipUseCase
import com.example.cskh.domain.usecase.GetEInvoiceViewUseCase
import com.example.cskh.domain.usecase.GetInvoiceDetailUseCase
import com.example.cskh.domain.usecase.UserFormPreferencesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class InvoiceDetailUiState(
    val detail: InvoiceDetail? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEInvoiceDownloading: Boolean = false,
    val eInvoiceMessage: String? = null,
    val eInvoiceError: String? = null,
    val eInvoiceData: EInvoiceData? = null,
    val isEInvoiceViewLoading: Boolean = false,
    val eInvoiceViewError: String? = null,
    /** true khi refresh token hết hạn → caller điều hướng về màn hình Login */
    val sessionExpired: Boolean = false,
)

class InvoiceDetailViewModel(
    private val getDetail: GetInvoiceDetailUseCase,
    private val formPreferences: UserFormPreferencesUseCase,
    private val downloadAndSaveEInvoiceZip: DownloadAndSaveEInvoiceZipUseCase,
    private val getEInvoiceView: GetEInvoiceViewUseCase,
    private val tokenRefresh: TokenRefreshCoordinator,
    private val invoiceId: Long,
) : ViewModel() {

    private val _state = MutableStateFlow(InvoiceDetailUiState())
    val state: StateFlow<InvoiceDetailUiState> = _state.asStateFlow()

    private val eInvoiceCache = mutableMapOf<Long, EInvoiceData>()

    init {
        load()
    }

    fun load() {
        val baseUrl = formPreferences.getBaseUrl()
        if (baseUrl.isBlank()) {
            _state.update { it.copy(errorMessage = "Thiếu địa chỉ API.") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = withContext(Dispatchers.Default) { getDetail(baseUrl, invoiceId) }
            
            if (isUnauthorized(result)) {
                if (!tokenRefresh.tryRefresh()) {
                    _state.update { it.copy(isLoading = false, sessionExpired = true) }
                    return@launch
                }
                load()
                return@launch
            }

            result.fold(
                onSuccess = { d ->
                    _state.update { it.copy(detail = d, isLoading = false) }
                    loadEInvoiceView()
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "Không tải được chi tiết")
                    }
                },
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun acknowledgeSessionExpired() {
        _state.update { it.copy(sessionExpired = false) }
    }

    fun onDownloadEInvoiceZip() {
        val baseUrl = formPreferences.getBaseUrl()
        if (baseUrl.isBlank()) {
            _state.update { it.copy(eInvoiceError = "Thiếu địa chỉ API.") }
            return
        }
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isEInvoiceDownloading = true,
                    eInvoiceError = null,
                    eInvoiceMessage = null,
                )
            }
            val result = withContext(Dispatchers.Default) { downloadAndSaveEInvoiceZip(baseUrl, invoiceId) }

            if (isUnauthorized(result)) {
                if (!tokenRefresh.tryRefresh()) {
                    _state.update { it.copy(isEInvoiceDownloading = false, sessionExpired = true) }
                    return@launch
                }
                onDownloadEInvoiceZip()
                return@launch
            }

            result.fold(
                onSuccess = { pathOrHint ->
                    _state.update {
                        it.copy(isEInvoiceDownloading = false, eInvoiceMessage = pathOrHint)
                    }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            isEInvoiceDownloading = false,
                            eInvoiceError = e.message ?: "Tải hóa đơn điện tử thất bại",
                        )
                    }
                },
            )
        }
    }

    fun clearEInvoiceFeedback() {
        _state.update { it.copy(eInvoiceMessage = null, eInvoiceError = null) }
    }

    fun loadEInvoiceView() {
        val baseUrl = formPreferences.getBaseUrl()
        if (baseUrl.isBlank()) return

        eInvoiceCache[invoiceId]?.let { cached ->
            _state.update { it.copy(eInvoiceData = cached, isEInvoiceViewLoading = false) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isEInvoiceViewLoading = true, eInvoiceViewError = null) }
            val result = withContext(Dispatchers.Default) { getEInvoiceView(baseUrl, invoiceId) }

            if (isUnauthorized(result)) {
                if (!tokenRefresh.tryRefresh()) {
                    _state.update { it.copy(isEInvoiceViewLoading = false, sessionExpired = true) }
                    return@launch
                }
                loadEInvoiceView()
                return@launch
            }

            result.fold(
                onSuccess = { data ->
                    eInvoiceCache[invoiceId] = data
                    _state.update { it.copy(eInvoiceData = data, isEInvoiceViewLoading = false) }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            isEInvoiceViewLoading = false,
                            eInvoiceViewError = e.message ?: "Không tải được hóa đơn điện tử",
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
}
