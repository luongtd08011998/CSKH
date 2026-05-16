package com.example.cskh

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {

    private var pendingArticleTitle by mutableStateOf<String?>(null)
    private var pendingArticleContent by mutableStateOf<String?>(null)
    private var pendingFeedbackId by mutableStateOf<Long?>(null)
    private var pendingInvoiceId by mutableStateOf<Long?>(null)
    private var pendingNavigateTo by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        logIntentExtras("onCreate", intent)

        val (title, content) = extractArticle(intent)
        pendingArticleTitle = title
        pendingArticleContent = content
        pendingFeedbackId = extractFeedbackId(intent)
        pendingInvoiceId = extractInvoiceId(intent)
        pendingNavigateTo = extractNavigateTo(intent)

        setContent {
            App(
                pendingArticleTitle = pendingArticleTitle,
                pendingArticleContent = pendingArticleContent,
                pendingFeedbackId = pendingFeedbackId,
                pendingInvoiceId = pendingInvoiceId,
                pendingNavigateTo = pendingNavigateTo,
                onNavigationHandled = {
                    pendingArticleTitle = null
                    pendingArticleContent = null
                    pendingFeedbackId = null
                    pendingInvoiceId = null
                    pendingNavigateTo = null
                }
            )
        }
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        logIntentExtras("onNewIntent", intent)
        
        val (title, content) = extractArticle(intent)
        pendingArticleTitle = title
        pendingArticleContent = content
        pendingFeedbackId = extractFeedbackId(intent)
        pendingInvoiceId = extractInvoiceId(intent)
        pendingNavigateTo = extractNavigateTo(intent)
    }

    private fun logIntentExtras(tag: String, intent: android.content.Intent?) {
        Log.d("FCM_NAV", "=== $tag ===")
        Log.d("FCM_NAV", "Action: ${intent?.action}")
        Log.d("FCM_NAV", "Data: ${intent?.data}")
        Log.d("FCM_NAV", "Flags: ${intent?.flags}")
        val extras = intent?.extras
        if (extras != null) {
            for (key in extras.keySet()) {
                Log.d("FCM_NAV", "  extra[$key] = ${extras.get(key)}")
            }
        } else {
            Log.d("FCM_NAV", "  (no extras)")
        }
        Log.d("FCM_NAV", "extractNavigateTo = ${extractNavigateTo(intent)}")
        Log.d("FCM_NAV", "extractFeedbackId = ${extractFeedbackId(intent)}")
        Log.d("FCM_NAV", "extractInvoiceId = ${extractInvoiceId(intent)}")
    }

    private fun extractArticle(intent: android.content.Intent?): Pair<String?, String?> {
        val title = intent?.getStringExtra("article_title")
        val content = intent?.getStringExtra("article_content")
        return Pair(title, content)
    }

    // Spec phananh_reply.md §6: referenceId từ FCM là String, parse sang Long trước khi dùng
    private fun extractFeedbackId(intent: android.content.Intent?): Long? {
        // Trường hợp 1: Mở từ PendingIntent do CskhFirebaseMessagingService tạo (LongExtra)
        val fromApp = intent?.getLongExtra("feedback_id", -1L)?.takeIf { it > 0 }
        if (fromApp != null) return fromApp

        // Trường hợp 2: Mở từ hệ thống Android - chỉ dùng referenceId khi type=FEEDBACK
        val type = intent?.getStringExtra("type") ?: ""
        if (!type.equals("FEEDBACK", ignoreCase = true)) return null

        val fromSystem = intent?.getStringExtra("referenceId")
        return fromSystem?.trim()?.toLongOrNull()?.takeIf { it > 0 }
    }

    private fun extractInvoiceId(intent: android.content.Intent?): Long? {
        val fromApp = intent?.getLongExtra("invoice_id", -1L)?.takeIf { it > 0 }
        if (fromApp != null) return fromApp

        val type = intent?.getStringExtra("type") ?: ""
        if (!type.equals("INVOICE", ignoreCase = true) && !type.equals("PAYMENT", ignoreCase = true) && !type.equals("DEBT_REMINDER", ignoreCase = true) && !type.equals("OVERDUE", ignoreCase = true) && !type.equals("WATER_CUTOFF", ignoreCase = true)) return null

        val fromSystem = intent?.getStringExtra("referenceId")
        return fromSystem?.trim()?.toLongOrNull()?.takeIf { it > 0 }
    }

    // Hóa đơn / Thanh toán / Nổi bật: đọc extra "navigate_to" hoặc suy luận từ type/tiêu đề
    private fun extractNavigateTo(intent: android.content.Intent?): String? {
        val extras = intent?.extras ?: return null

        // 1. Kiểm tra trực tiếp key "navigate_to" (được đặt bởi PendingIntent của FCM Service)
        val direct = intent.getStringExtra("navigate_to") ?: extras.getString("navigate_to")
        if (!direct.isNullOrBlank()) return direct

        // 2. Suy luận từ "type" (do hệ thống đặt khi app ở background/killed)
        val type = intent.getStringExtra("type") ?: extras.getString("type")
        if (type != null) {
            when (type.uppercase()) {
                "PAYMENT", "INVOICE", "DEBT_REMINDER", "OVERDUE", "WATER_CUTOFF" -> return "notifications_billing"
                "MAINTENANCE", "WATER_CUT" -> return "notifications_maintenance"
                "NOTIFICATION", "FEATURED" -> return "notifications_featured"
            }
        }

        // 3. Fallback: kiểm tra tiêu đề thông báo (khi type không có trong data payload)
        val title = intent.getStringExtra("gcm.notification.title")
            ?: extras.getString("gcm.notification.title")
            ?: intent.getStringExtra("title")
            ?: extras.getString("title")
        if (title != null) {
            if (title.contains("Hóa đơn", ignoreCase = true) || title.contains("Thanh toán", ignoreCase = true) || title.contains("quá hạn", ignoreCase = true) || title.contains("cúp nước", ignoreCase = true)) {
                return "notifications_billing"
            }
            if (title.contains("nổi bật", ignoreCase = true) || title.contains("Bài viết", ignoreCase = true)) {
                return "notifications_featured"
            }
        }

        return null
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}