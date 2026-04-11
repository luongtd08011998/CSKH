package com.example.cskh.presentation.screens.home

import androidx.lifecycle.ViewModel
import com.example.cskh.data.session.SessionManager

class HomeViewModel(
    private val sessionManager: SessionManager,
) : ViewModel() {

    fun logout() {
        sessionManager.clear()
    }
}
