package com.example.cskh

import androidx.compose.ui.window.ComposeUIViewController
import com.example.cskh.platform.IosNotificationBridge
import com.example.cskh.platform.PickerPresenter
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    val vc = ComposeUIViewController {
        val bridge = IosNotificationBridge
        App(
            pendingArticleTitle = bridge.pendingArticleTitle,
            pendingArticleContent = bridge.pendingArticleContent,
            pendingFeedbackId = bridge.pendingFeedbackId,
            pendingInvoiceId = bridge.pendingInvoiceId,
            pendingNavigateTo = bridge.pendingNavigateTo,
            onNavigationHandled = {
                bridge.clearPending()
            }
        )
    }
    PickerPresenter.rootViewController = vc
    return vc
}
