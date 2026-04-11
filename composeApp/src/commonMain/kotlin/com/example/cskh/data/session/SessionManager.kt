package com.example.cskh.data.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManager {

    private val _tokenFlow = MutableStateFlow<String?>(null)
    val tokenFlow: StateFlow<String?> = _tokenFlow.asStateFlow()

    val accessToken: String?
        get() = _tokenFlow.value

    fun setToken(token: String?) {
        _tokenFlow.value = token
    }

    fun clear() {
        setToken(null)
    }
}
