package com.example.cskh.presentation.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cskh.domain.repository.RegisterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val name: String = "",
    val phone: String = "",
    val waterMeterAddress: String = "",
    val email: String = "",
    val isSubmitting: Boolean = false,
    val registrationTime: String? = null,
    val errorMessage: String? = null,
)

class RegisterViewModel(
    private val registerRepository: RegisterRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    fun onNameChange(value: String) = _state.update { it.copy(name = value) }
    fun onPhoneChange(value: String) = _state.update { it.copy(phone = value) }
    fun onAddressChange(value: String) = _state.update { it.copy(waterMeterAddress = value) }
    fun onEmailChange(value: String) = _state.update { it.copy(email = value) }

    fun clearError() = _state.update { it.copy(errorMessage = null) }

    fun resetForm() = _state.update {
        RegisterUiState()
    }

    fun submit() {
        val s = _state.value
        if (s.isSubmitting) return

        if (!s.name.trim().contains(" ")) {
            _state.update { it.copy(errorMessage = "Vui lòng nhập đầy đủ họ và tên (phải có ít nhất 2 từ)") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, errorMessage = null, registrationTime = null) }
            registerRepository.submitRegistration(
                name = s.name.trim(),
                phone = s.phone.trim(),
                waterMeterAddress = s.waterMeterAddress.trim(),
                email = s.email.trim(),
            ).fold(
                onSuccess = {
                    // Sau khi đăng ký thành công, lấy thời gian đăng ký
                    registerRepository.getRegistrationTime(s.phone.trim()).fold(
                        onSuccess = { time ->
                            _state.update { it.copy(isSubmitting = false, registrationTime = time) }
                        },
                        onFailure = {
                            _state.update { it.copy(isSubmitting = false, registrationTime = it.registrationTime) }
                        },
                    )
                },
                onFailure = { e ->
                    _state.update { it.copy(isSubmitting = false, errorMessage = e.message ?: "Đăng ký thất bại") }
                },
            )
        }
    }
}
