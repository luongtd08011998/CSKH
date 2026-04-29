package com.example.cskh.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.WebKit.WKWebView

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@Composable
actual fun HtmlContentView(
    html: String,
    modifier: Modifier,
) {
    val fullHtml = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                * { margin: 0; padding: 0; box-sizing: border-box; }
                body {
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                    font-size: 14px;
                    line-height: 1.5;
                    color: #212121;
                    padding: 0;
                    word-wrap: break-word;
                    overflow-wrap: break-word;
                }
                img { max-width: 100%; height: auto; }
                table { max-width: 100%; overflow-x: auto; display: block; }
                a { color: #1976D2; }
            </style>
        </head>
        <body>$html</body>
        </html>
    """.trimIndent()

    UIKitView(
        factory = {
            WKWebView().apply {
                isOpaque = false
                backgroundColor = platform.UIColor.clearColor
                scrollView.isScrollEnabled = false
                loadHTMLString(fullHtml, baseURL = null)
            }
        },
        update = { webView ->
            webView.loadHTMLString(fullHtml, baseURL = null)
        },
        modifier = modifier,
    )
}
