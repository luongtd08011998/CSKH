package com.example.cskh.presentation.screens.phananh

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cskh.domain.usecase.CreateFeedbackUseCase
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
)

class PhanAnhViewModel(
    private val createFeedback: CreateFeedbackUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(PhanAnhUiState())
    val state: StateFlow<PhanAnhUiState> = _state.asStateFlow()

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun resetSubmitted() {
        _state.update { it.copy(submittedTrackingCode = null, errorMessage = null) }
    }

    fun submit(
        issueType: String,
        location: String,
        description: String,
        images: List<PickedImage>,
    ) {
        if (_state.value.isSubmitting) return
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, errorMessage = null) }
            createFeedback(issueType, location, description, images)
                .fold(
                    onSuccess = { tracking ->
                        _state.update { it.copy(isSubmitting = false, submittedTrackingCode = tracking) }
                    },
                    onFailure = { e ->
                        _state.update { it.copy(isSubmitting = false, errorMessage = e.message ?: "Gửi phản ánh thất bại") }
                    },
                )
        }
    }
}

