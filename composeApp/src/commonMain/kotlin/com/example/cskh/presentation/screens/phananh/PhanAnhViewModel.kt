package com.example.cskh.presentation.screens.phananh

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cskh.data.session.TokenRefreshCoordinator
import com.example.cskh.domain.usecase.CreateFeedbackUseCase
import com.example.cskh.domain.usecase.UserFormPreferencesUseCase
import com.example.cskh.platform.PickedImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PhanAnhUiState(
    val isSubmitting: Boolean = false,
    val submittedTrackingCode: String? = null,
    val errorMessage: String? = null,
    /** true khi refresh token hết hạn → caller điều hướng về màn hình Login */
    val sessionExpired: Boolean = false,
)

class PhanAnhViewModel(
    private val createFeedback: CreateFeedbackUseCase,
    private val formPreferences: UserFormPreferencesUseCase,
    private val tokenRefresh: TokenRefreshCoordinator,
) : ViewModel() {
    private val _state = MutableStateFlow(PhanAnhUiState())
    val state: StateFlow<PhanAnhUiState> = _state.asStateFlow()

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun resetSubmitted() {
        _state.update { it.copy(submittedTrackingCode = null, errorMessage = null) }
    }

    fun acknowledgeSessionExpired() {
        _state.update { it.copy(sessionExpired = false) }
    }

    fun submit(
        issueType: String,
        location: String,
        description: String,
        images: List<PickedImage>,
    ) {
        if (_state.value.isSubmitting) return
        val baseUrl = formPreferences.getBaseUrl()
        if (baseUrl.isBlank()) {
            _state.update { it.copy(errorMessage = "Thiếu địa chỉ API. Vui lòng đăng nhập lại.") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, errorMessage = null) }
            val result = createFeedback(issueType, location, description, images)
            
            if (isUnauthorized(result)) {
                if (!tokenRefresh.tryRefresh()) {
                    _state.update { it.copy(isSubmitting = false, sessionExpired = true) }
                    return@launch
                }
                // Thử lại sau khi refresh thành công
                submit(issueType, location, description, images)
                return@launch
            }

            result.fold(
                onSuccess = { tracking ->
                    _state.update { it.copy(isSubmitting = false, submittedTrackingCode = tracking) }
                },
                onFailure = { e ->
                    _state.update { it.copy(isSubmitting = false, errorMessage = e.message ?: "Gửi phản ánh thất bại") }
                },
            )
        }
    }

    private fun isUnauthorized(result: Result<*>): Boolean =
        result.exceptionOrNull()?.message?.let {
            it.contains("401") || it.contains("UNAUTHORIZED_401") || it.contains("Chưa đăng nhập")
        } == true
}
