package com.example.cskh.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class NavigationEvent {
    data class FeedbackDetail(val id: Long) : NavigationEvent()
    data class InvoiceDetail(val id: Long) : NavigationEvent()
}

class PushNavigationBus {
    private val _events = MutableSharedFlow<NavigationEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    fun navigateToFeedback(id: Long) {
        _events.tryEmit(NavigationEvent.FeedbackDetail(id))
    }

    fun navigateToInvoice(id: Long) {
        _events.tryEmit(NavigationEvent.InvoiceDetail(id))
    }
}
