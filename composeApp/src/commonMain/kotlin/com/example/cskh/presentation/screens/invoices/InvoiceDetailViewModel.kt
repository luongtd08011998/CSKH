package com.example.cskh.presentation.screens.invoices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cskh.domain.model.InvoiceDetail
import com.example.cskh.domain.usecase.DownloadAndSaveEInvoiceZipUseCase
import com.example.cskh.domain.usecase.GetInvoiceDetailUseCase
import com.example.cskh.domain.usecase.UserFormPreferencesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InvoiceDetailUiState(
    val detail: InvoiceDetail? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEInvoiceDownloading: Boolean = false,
    val eInvoiceMessage: String? = null,
    val eInvoiceError: String? = null,
)

class InvoiceDetailViewModel(
    private val getDetail: GetInvoiceDetailUseCase,
    private val formPreferences: UserFormPreferencesUseCase,
    private val downloadAndSaveEInvoiceZip: DownloadAndSaveEInvoiceZipUseCase,
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
            val result = getDetail(baseUrl, invoiceId)
            result.fold(
                onSuccess = { d ->
                    _state.update { it.copy(detail = d, isLoading = false) }
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
            downloadAndSaveEInvoiceZip(baseUrl, invoiceId).fold(
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
}
