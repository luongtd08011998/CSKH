package com.example.cskh.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cskh.data.session.SessionManager
import com.example.cskh.domain.usecase.LoginUseCase
import com.example.cskh.domain.usecase.UserFormPreferencesUseCase
import com.example.cskh.platform.FcmDeviceSync
import com.example.cskh.platform.defaultDevMachineApiBaseUrl
import com.example.cskh.presentation.CompanyBranding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val digiCode: String = "",
    val phone: String = "",
    val rememberLogin: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val formPreferences: UserFormPreferencesUseCase,
    private val sessionManager: SessionManager,
    private val fcmDeviceSync: FcmDeviceSync,
) : ViewModel() {

    private val _state = MutableStateFlow(
        LoginUiState(
            digiCode = formPreferences.getDigiCode(),
            phone = formPreferences.getPhone(),
        ),
    )
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onDigiCodeChange(value: String) {
        _state.update { it.copy(digiCode = value, errorMessage = null) }
    }

    fun onPhoneChange(value: String) {
        _state.update { it.copy(phone = value, errorMessage = null) }
    }

    fun onRememberLoginChange(value: Boolean) {
        _state.update { it.copy(rememberLogin = value) }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun login(onSuccess: () -> Unit) {
        val s = _state.value
        if (s.isLoading) return
        val baseUrl = defaultDevMachineApiBaseUrl(CompanyBranding.DEV_API_PORT)
        if (s.digiCode.isBlank() || s.phone.isBlank()) {
            _state.update { it.copy(errorMessage = "Vui lòng nhập đủ mã và số điện thoại") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = loginUseCase(baseUrl, s.digiCode.trim(), s.phone.trim())
            result.fold(
                onSuccess = { (accessToken, refreshToken) ->
                    if (s.rememberLogin) {
                        formPreferences.saveForm(s.digiCode.trim(), s.phone.trim(), baseUrl)
                    } else {
                        formPreferences.saveForm("", "", baseUrl)
                    }
                    // Lưu cả hai token vào persistent storage
                    formPreferences.saveAccessToken(accessToken)
                    formPreferences.saveRefreshToken(refreshToken)
                    // Cập nhật session in-memory
                    sessionManager.setToken(accessToken, refreshToken)
                    fcmDeviceSync.registerIfLoggedIn()
                    _state.update { it.copy(isLoading = false) }
                    onSuccess()
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "Đăng nhập thất bại")
                    }
                },
            )
        }
    }
}
