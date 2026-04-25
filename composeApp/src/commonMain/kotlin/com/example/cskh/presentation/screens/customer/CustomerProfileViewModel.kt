package com.example.cskh.presentation.screens.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cskh.data.session.SessionManager
import com.example.cskh.domain.model.CustomerProfile
import com.example.cskh.domain.usecase.GetCustomerMeUseCase
import com.example.cskh.domain.usecase.UserFormPreferencesUseCase
import com.example.cskh.presentation.NotificationBadgeStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CustomerProfileUiState(
    val profile: CustomerProfile? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class CustomerProfileViewModel(
    private val getCustomerMe: GetCustomerMeUseCase,
    private val formPreferences: UserFormPreferencesUseCase,
    private val sessionManager: SessionManager,
    private val notificationBadgeStore: NotificationBadgeStore,
) : ViewModel() {

    private val _state = MutableStateFlow(CustomerProfileUiState())
    val state: StateFlow<CustomerProfileUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun logout() {
        notificationBadgeStore.clear()
        formPreferences.clearAccessToken()
        sessionManager.clear()
    }

    fun refresh() {
        val baseUrl = formPreferences.getBaseUrl()
        if (baseUrl.isBlank()) {
            _state.update { it.copy(errorMessage = "Thiếu địa chỉ API. Vui lòng đăng nhập lại.") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            getCustomerMe(baseUrl).fold(
                onSuccess = { profile ->
                    _state.update { it.copy(profile = profile, isLoading = false) }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Không tải được thông tin khách hàng",
                        )
                    }
                },
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }
}
