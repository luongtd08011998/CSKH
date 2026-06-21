package com.example.cskh

import androidx.compose.runtime.getValue
import androidx.compose.ui.window.ComposeUIViewController
import com.example.cskh.platform.IosNotificationBridge
import com.example.cskh.platform.PickerPresenter
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    val vc = ComposeUIViewController(
        configure = {
            onFocusBehavior = androidx.compose.ui.uikit.OnFocusBehavior.DoNothing
        }
    ) {
        // Đọc trực tiếp từ MutableState của IosNotificationBridge bên trong
        // Composable body — Compose sẽ recompose và re-trigger LaunchedEffect
        // mỗi khi Bridge được cập nhật (foreground tap, background tap, cold start).
        val bridge = IosNotificationBridge
        val pendingArticleTitle by bridge._pendingArticleTitle
        val pendingArticleContent by bridge._pendingArticleContent
        val pendingFeedbackId by bridge._pendingFeedbackId
        val pendingInvoiceId by bridge._pendingInvoiceId
        val pendingNavigateTo by bridge._pendingNavigateTo

        App(
            pendingArticleTitle = pendingArticleTitle,
            pendingArticleContent = pendingArticleContent,
            pendingFeedbackId = pendingFeedbackId,
            pendingInvoiceId = pendingInvoiceId,
            pendingNavigateTo = pendingNavigateTo,
            onNavigationHandled = {
                bridge.clearPending()
            }
        )
    }
    PickerPresenter.rootViewController = vc
    return vc
}

