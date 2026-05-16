package com.example.cskh.platform

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

internal object IosNotificationBridge {
    internal val _pendingArticleTitle: MutableState<String?> = mutableStateOf(null)
    internal val _pendingArticleContent: MutableState<String?> = mutableStateOf(null)
    internal val _pendingFeedbackId: MutableState<Long?> = mutableStateOf(null)
    internal val _pendingInvoiceId: MutableState<Long?> = mutableStateOf(null)
    internal val _pendingNavigateTo: MutableState<String?> = mutableStateOf(null)

    val pendingArticleTitle: String? get() = _pendingArticleTitle.value
    val pendingArticleContent: String? get() = _pendingArticleContent.value
    val pendingFeedbackId: Long? get() = _pendingFeedbackId.value
    val pendingInvoiceId: Long? get() = _pendingInvoiceId.value
    val pendingNavigateTo: String? get() = _pendingNavigateTo.value

    fun setNotificationData(
        articleTitle: String?,
        articleContent: String?,
        feedbackId: Long?,
        invoiceId: Long?,
        navigateTo: String?,
    ) {
        _pendingArticleTitle.value = articleTitle
        _pendingArticleContent.value = articleContent
        _pendingFeedbackId.value = feedbackId
        _pendingInvoiceId.value = invoiceId
        _pendingNavigateTo.value = navigateTo
    }

    fun clearPending() {
        _pendingArticleTitle.value = null
        _pendingArticleContent.value = null
        _pendingFeedbackId.value = null
        _pendingInvoiceId.value = null
        _pendingNavigateTo.value = null
    }
}

fun setIosNotificationData(
    articleTitle: String?,
    articleContent: String?,
    feedbackId: Long?,
    invoiceId: Long?,
    navigateTo: String?,
) {
    IosNotificationBridge.setNotificationData(
        articleTitle, articleContent, feedbackId, invoiceId, navigateTo
    )
}
