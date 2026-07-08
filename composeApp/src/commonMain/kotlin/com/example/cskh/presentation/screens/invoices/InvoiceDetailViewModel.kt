package com.example.cskh.presentation.screens.invoices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cskh.data.session.TokenRefreshCoordinator
import com.example.cskh.domain.model.EInvoiceData
import com.example.cskh.domain.model.InvoiceDetail
import com.example.cskh.domain.usecase.DownloadEInvoicePdfUseCase
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
    val pdfData: ByteArray? = null,
    /** true khi refresh token hết hạn → caller điều hướng về màn hình Login */
    val sessionExpired: Boolean = false,
)

class InvoiceDetailViewModel(
    private val getInvoiceDetailUseCase: GetInvoiceDetailUseCase,
    private val downloadEInvoicePdf: DownloadEInvoicePdfUseCase,
    private val formPreferences: UserFormPreferencesUseCase,
    private val zipSaver: com.example.cskh.platform.InvoiceZipSaver,
    private val tokenRefresh: TokenRefreshCoordinator,
    private val invoiceId: Long,
) : ViewModel() {

    private val _state = MutableStateFlow(InvoiceDetailUiState())
    val state: StateFlow<InvoiceDetailUiState> = _state.asStateFlow()

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
            val result = withContext(Dispatchers.Default) { getInvoiceDetailUseCase(baseUrl, invoiceId) }
            
            if (isUnauthorized(result)) {
                if (!tokenRefresh.tryRefresh()) {
                    _state.update { it.copy(isLoading = false, sessionExpired = true) }
                    return@launch
                }
                val retry = withContext(Dispatchers.Default) { getInvoiceDetailUseCase(baseUrl, invoiceId) }
                if (retry.isSuccess) {
                    _state.update { it.copy(detail = retry.getOrNull(), isLoading = false) }
                } else {
                    _state.update {
                        it.copy(
                            errorMessage = retry.exceptionOrNull()?.message ?: "Lỗi tải chi tiết hóa đơn",
                            isLoading = false,
                        )
                    }
                }
                return@launch
            }
            
            if (result.isSuccess) {
                _state.update { it.copy(detail = result.getOrNull(), isLoading = false) }
            } else {
                _state.update {
                    it.copy(
                        errorMessage = result.exceptionOrNull()?.message ?: "Lỗi không xác định",
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun acknowledgeSessionExpired() {
        _state.update { it.copy(sessionExpired = false) }
    }

    fun downloadEInvoice() {
        val baseUrl = formPreferences.getBaseUrl()
        if (baseUrl.isBlank()) {
            _state.update { it.copy(eInvoiceError = "Thiếu địa chỉ API.") }
            return
        }
        _state.update {
            it.copy(
                isEInvoiceDownloading = true,
                eInvoiceMessage = null,
                eInvoiceError = null,
            )
        }
        viewModelScope.launch {
            val result = withContext(Dispatchers.Default) { downloadEInvoicePdf(baseUrl, invoiceId) }
            
            if (isUnauthorized(result)) {
                if (!tokenRefresh.tryRefresh()) {
                    _state.update { it.copy(isEInvoiceDownloading = false, sessionExpired = true) }
                    return@launch
                }
                val retry = withContext(Dispatchers.Default) { downloadEInvoicePdf(baseUrl, invoiceId) }
                handleDownloadResult(retry)
                return@launch
            }
            handleDownloadResult(result)
        }
    }

    private fun handleDownloadResult(result: Result<ByteArray>) {
        result.fold(
            onSuccess = { bytes ->
                viewModelScope.launch {
                    val saveResult = zipSaver.saveAndOpenPdf(invoiceId, bytes)
                    saveResult.fold(
                        onSuccess = { msg ->
                            _state.update {
                                it.copy(isEInvoiceDownloading = false, eInvoiceMessage = msg)
                            }
                        },
                        onFailure = { e ->
                            _state.update {
                                it.copy(isEInvoiceDownloading = false, eInvoiceError = e.message ?: "Tải hóa đơn điện tử thất bại")
                            }
                        }
                    )
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

    fun clearPdfData() {
        _state.update { it.copy(pdfData = null) }
    }

    fun clearEInvoiceFeedback() {
        _state.update { it.copy(eInvoiceMessage = null, eInvoiceError = null) }
    }

    private fun isUnauthorized(result: Result<*>): Boolean =
        result.exceptionOrNull()?.message?.let {
            it.contains("401") || it.contains("UNAUTHORIZED_401") || it.contains("Chưa đăng nhập")
        } == true
}
