package com.example.cskh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val (title, content) = extractArticle(intent)
        val feedbackId = extractFeedbackId(intent)
        val navigateTo = extractNavigateTo(intent)
        setContent {
            App(
                pendingArticleTitle = title,
                pendingArticleContent = content,
                pendingFeedbackId = feedbackId,
                pendingNavigateTo = navigateTo,
            )
        }
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val (title, content) = extractArticle(intent)
        val feedbackId = extractFeedbackId(intent)
        val navigateTo = extractNavigateTo(intent)
        setContent {
            App(
                pendingArticleTitle = title,
                pendingArticleContent = content,
                pendingFeedbackId = feedbackId,
                pendingNavigateTo = navigateTo,
            )
        }
    }

    private fun extractArticle(intent: android.content.Intent?): Pair<String?, String?> {
        val title = intent?.getStringExtra("article_title")
        val content = intent?.getStringExtra("article_content")
        return Pair(title, content)
    }

    // Spec phananh_reply.md §6: referenceId từ FCM là String, parse sang Long trước khi dùng
    private fun extractFeedbackId(intent: android.content.Intent?): Long? {
        return intent?.getLongExtra("feedback_id", -1L)?.takeIf { it > 0 }
    }

    // Hóa đơn / Thanh toán: đọc extra "navigate_to" được đặt bởi FCM Service
    private fun extractNavigateTo(intent: android.content.Intent?): String? {
        return intent?.getStringExtra("navigate_to")
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}