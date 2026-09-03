package com.example.cskh.presentation.screens.changeinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cskh.data.session.TokenRefreshCoordinator
import com.example.cskh.domain.model.CustomerProfile
import com.example.cskh.domain.usecase.CreateFeedbackUseCase
import com.example.cskh.domain.usecase.GetCustomerMeUseCase
import com.example.cskh.domain.usecase.UserFormPreferencesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChangeInfoUiState(
    val profile: CustomerProfile? = null,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val sessionExpired: Boolean = false,
)

class ChangeInfoViewModel(
    private val getCustomerMe: GetCustomerMeUseCase,
    private val createFeedback: CreateFeedbackUseCase,
    private val formPreferences: UserFormPreferencesUseCase,
    private val tokenRefresh: TokenRefreshCoordinator,
) : ViewModel() {

    private val _state = MutableStateFlow(ChangeInfoUiState())
    val state: StateFlow<ChangeInfoUiState> = _state.asStateFlow()

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
            val result = getCustomerMe(baseUrl)

            if (isUnauthorized(result)) {
                if (!tokenRefresh.tryRefresh()) {
                    _state.update { it.copy(isLoading = false, sessionExpired = true) }
                    return@launch
                }
                refresh()
                return@launch
            }

            result.fold(
                onSuccess = { profile ->
                    _state.update { it.copy(profile = profile, isLoading = false) }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "Không tải được thông tin.")
                    }
                },
            )
        }
    }

    fun submitRequest(
        currentProfile: CustomerProfile?,
        requestContent: String,
        onSuccess: () -> Unit,
    ) {
        if (_state.value.isSubmitting) return
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, errorMessage = null) }

            val description = buildString {
                appendLine("=== YÊU CẦU THAY ĐỔI THÔNG TIN ===")
                appendLine()
                appendLine("Khách hàng: ${currentProfile?.name ?: "—"}")
                appendLine("Mã KH: ${currentProfile?.digiCode ?: "—"}")
                appendLine("SĐT: ${currentProfile?.phone ?: "—"}")
                appendLine()
                appendLine("Nội dung yêu cầu:")
                append(requestContent)
            }

            val result = createFeedback(
                issueType = "other",
                location = currentProfile?.address ?: "",
                description = description,
                images = emptyList(),
            )

            if (isUnauthorized(result)) {
                if (!tokenRefresh.tryRefresh()) {
                    _state.update { it.copy(isSubmitting = false, sessionExpired = true) }
                    return@launch
                }
                submitRequest(currentProfile, requestContent, onSuccess)
                return@launch
            }

            result.fold(
                onSuccess = {
                    _state.update { it.copy(isSubmitting = false) }
                    onSuccess()
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(isSubmitting = false, errorMessage = e.message ?: "Gửi yêu cầu thất bại. Vui lòng thử lại.")
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

    private fun isUnauthorized(result: Result<*>): Boolean =
        result.exceptionOrNull()?.message?.let {
            it.contains("401") || it.contains("UNAUTHORIZED_401") || it.contains("Chưa đăng nhập")
        } == true
}
