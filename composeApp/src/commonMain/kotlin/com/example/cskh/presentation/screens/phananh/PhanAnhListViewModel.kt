package com.example.cskh.presentation.screens.phananh

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cskh.domain.model.FeedbackItem
import com.example.cskh.domain.usecase.GetFeedbacksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PhanAnhListUiState(
    val isLoading: Boolean = true,
    val feedbacks: List<FeedbackItem> = emptyList(),
    val errorMessage: String? = null,
)

class PhanAnhListViewModel(
    private val getFeedbacks: GetFeedbacksUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(PhanAnhListUiState())
    val state: StateFlow<PhanAnhListUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun refresh() = load()

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            getFeedbacks()
                .fold(
                    onSuccess = { list ->
                        _state.update { it.copy(isLoading = false, feedbacks = list) }
                    },
                    onFailure = { e ->
                        _state.update {
                            it.copy(isLoading = false, errorMessage = e.message ?: "Không tải được danh sách phản ánh")
                        }
                    },
                )
        }
    }
}
