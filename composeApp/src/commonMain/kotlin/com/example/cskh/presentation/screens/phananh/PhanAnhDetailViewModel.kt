package com.example.cskh.presentation.screens.phananh

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cskh.domain.model.FeedbackDetail
import com.example.cskh.domain.usecase.GetFeedbackDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PhanAnhDetailUiState(
    val isLoading: Boolean = true,
    val detail: FeedbackDetail? = null,
    val errorMessage: String? = null,
)

class PhanAnhDetailViewModel(
    private val getFeedbackDetail: GetFeedbackDetailUseCase,
    private val feedbackId: Long,
) : ViewModel() {
    private val _state = MutableStateFlow(PhanAnhDetailUiState())
    val state: StateFlow<PhanAnhDetailUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun refresh() = load()

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            getFeedbackDetail(feedbackId).fold(
                onSuccess = { detail ->
                    _state.update { it.copy(isLoading = false, detail = detail) }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "Không tải được chi tiết phản ánh")
                    }
                },
            )
        }
    }
}
