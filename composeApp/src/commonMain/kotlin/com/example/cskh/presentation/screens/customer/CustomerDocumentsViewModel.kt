package com.example.cskh.presentation.screens.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cskh.data.session.TokenRefreshCoordinator
import com.example.cskh.domain.model.CustomerDocument
import com.example.cskh.domain.usecase.DownloadCustomerDocUseCase
import com.example.cskh.domain.usecase.GetCustomerDocListUseCase
import com.example.cskh.domain.usecase.GetCustomerMeUseCase
import com.example.cskh.domain.usecase.UserFormPreferencesUseCase
import com.example.cskh.platform.InvoiceZipSaver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CustomerDocumentsUiState(
    val customerName: String = "",
    val digiCode: String = "",
    val documents: List<CustomerDocument> = emptyList(),
    val selectedDoc: CustomerDocument? = null,
    val pdfFilePath: String? = null,
    val isLoading: Boolean = false,
    val isDownloadingPdf: Boolean = false,
    val errorMessage: String? = null,
)

class CustomerDocumentsViewModel(
    private val getCustomerMe: GetCustomerMeUseCase,
    private val getCustomerDocList: GetCustomerDocListUseCase,
    private val downloadCustomerDoc: DownloadCustomerDocUseCase,
    private val formPreferences: UserFormPreferencesUseCase,
    private val tokenRefresh: TokenRefreshCoordinator,
    private val invoiceZipSaver: InvoiceZipSaver,
) : ViewModel() {

    private val _state = MutableStateFlow(CustomerDocumentsUiState())
    val state: StateFlow<CustomerDocumentsUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            var code = formPreferences.getDigiCode()
            var phone = formPreferences.getPhone()
            val baseUrl = formPreferences.getBaseUrl()

            if (baseUrl.isNotBlank()) {
                val profileResult = getCustomerMe(baseUrl)
                profileResult.onSuccess { profile ->
                    if (profile.digiCode.isNotBlank()) code = profile.digiCode
                    if (profile.phone.isNotBlank()) phone = profile.phone
                    _state.update {
                        it.copy(
                            customerName = profile.name,
                            digiCode = profile.digiCode.ifBlank { code },
                        )
                    }
                }
            }

            if (code.isBlank() || phone.isBlank()) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Không tìm thấy thông tin mã khách hàng hoặc số điện thoại.",
                    )
                }
                return@launch
            }

            getCustomerDocList(code, phone).fold(
                onSuccess = { docs ->
                    val primaryDoc = docs.firstOrNull { it.filename.endsWith(".pdf", ignoreCase = true) }
                        ?: docs.firstOrNull()

                    _state.update {
                        it.copy(
                            documents = docs,
                            isLoading = false,
                            errorMessage = null,
                            digiCode = code,
                        )
                    }

                    if (primaryDoc != null) {
                        loadAndDisplayPdf(primaryDoc)
                    }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Không thể tải danh sách hồ sơ khách hàng.",
                            digiCode = code,
                        )
                    }
                },
            )
        }
    }

    fun loadAndDisplayPdf(doc: CustomerDocument) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    selectedDoc = doc,
                    isDownloadingPdf = true,
                    errorMessage = null,
                )
            }

            downloadCustomerDoc(doc.fullname).fold(
                onSuccess = { bytes ->
                    invoiceZipSaver.saveDocumentPdf(doc.filename, bytes).fold(
                        onSuccess = { filePath ->
                            _state.update {
                                it.copy(
                                    pdfFilePath = filePath,
                                    isDownloadingPdf = false,
                                )
                            }
                        },
                        onFailure = { e ->
                            _state.update {
                                it.copy(
                                    isDownloadingPdf = false,
                                    errorMessage = e.message ?: "Không thể lưu tệp PDF.",
                                )
                            }
                        }
                    )
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            isDownloadingPdf = false,
                            errorMessage = e.message ?: "Không thể tải tệp PDF.",
                        )
                    }
                }
            )
        }
    }

    fun openInExternalViewer() {
        val doc = _state.value.selectedDoc ?: return
        viewModelScope.launch {
            downloadCustomerDoc(doc.fullname).onSuccess { bytes ->
                invoiceZipSaver.saveAndOpenDocumentPdf(doc.filename, bytes)
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }
}
