package com.example.cskh.data.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManager {

    private val _tokenFlow = MutableStateFlow<String?>(null)
    val tokenFlow: StateFlow<String?> = _tokenFlow.asStateFlow()

    val accessToken: String?
        get() = _tokenFlow.value

    /** RefreshToken lưu in-memory (cũng được persist vào UserPreferences riêng) */
    var refreshToken: String? = null
        private set

    fun setToken(accessToken: String?, refreshToken: String? = null) {
        _tokenFlow.value = accessToken
        if (refreshToken != null) this.refreshToken = refreshToken
    }

    /** Gọi sau khi /auth/refresh thành công – chỉ cập nhật accessToken mới */
    fun updateAccessToken(newAccessToken: String) {
        _tokenFlow.value = newAccessToken
    }

    fun clear() {
        _tokenFlow.value = null
        refreshToken = null
    }
}
