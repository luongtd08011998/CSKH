package com.example.cskh.platform

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
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

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                setInitialScale(100)
                setBackgroundColor(0x00000000)
                webViewClient = WebViewClient()
                loadDataWithBaseURL(null, fullHtml, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(null, fullHtml, "text/html", "UTF-8", null)
        },
        modifier = modifier,
    )
}
