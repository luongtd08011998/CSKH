package com.example.cskh.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURL
import platform.WebKit.WKWebView

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@Composable
actual fun PdfDocumentView(
    filePath: String,
    modifier: Modifier,
) {
    UIKitView(
        factory = {
            WKWebView().apply {
                val fileUrl = NSURL.fileURLWithPath(filePath)
                val dirUrl = fileUrl.URLByDeletingLastPathComponent
                if (dirUrl != null) {
                    loadFileURL(fileUrl, allowingReadAccessToURL = dirUrl)
                }
            }
        },
        update = { webView ->
            val fileUrl = NSURL.fileURLWithPath(filePath)
            val dirUrl = fileUrl.URLByDeletingLastPathComponent
            if (dirUrl != null) {
                webView.loadFileURL(fileUrl, allowingReadAccessToURL = dirUrl)
            }
        },
        modifier = modifier,
    )
}
