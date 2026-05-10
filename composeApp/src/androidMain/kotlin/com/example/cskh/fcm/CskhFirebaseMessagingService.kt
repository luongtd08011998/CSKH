package com.example.cskh.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.example.cskh.R
import com.example.cskh.MainActivity
import com.example.cskh.platform.FcmDeviceSync
import com.example.cskh.util.PushNavigationBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class CskhFirebaseMessagingService : FirebaseMessagingService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        serviceScope.launch {
            runCatching {
                GlobalContext.get().get<FcmDeviceSync>().registerWithFcmToken(token)
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: message.data["title"] ?: getString(R.string.app_name)
        val body = message.notification?.body ?: message.data["body"] ?: ""
        val url = message.data["URL"]?.trim().orEmpty()
        val type = message.data["type"]?.trim().orEmpty()
        val referenceId = message.data["referenceId"]?.trim().orEmpty()

        if (type.equals("ARTICLE", ignoreCase = true) && referenceId.isNotBlank()) {
            showArticleNotification(
                title = title,
                body = body,
                articleTitle = message.data["articleTitle"] ?: title,
                articleContent = message.data["articleContent"] ?: body,
            )
            return
        }

        // Spec phananh_reply.md: type=FEEDBACK → deep link đến PhanAnhDetailScreen
        if (type.equals("FEEDBACK", ignoreCase = true) && referenceId.isNotBlank()) {
            val feedbackId = referenceId.toLongOrNull()
            if (feedbackId != null) {
                showFeedbackNotification(title = title, body = body, feedbackId = feedbackId)
                GlobalContext.get().get<PushNavigationBus>().navigateToFeedback(feedbackId)
                return
            }
        }

        if (type.equals("INVOICE", ignoreCase = true) || type.equals("PAYMENT", ignoreCase = true) || type.equals("DEBT_REMINDER", ignoreCase = true)) {
            val invoiceId = referenceId.toLongOrNull()
            showInvoiceNotification(title = title, body = body, invoiceId = invoiceId)
            if (invoiceId != null) {
                GlobalContext.get().get<PushNavigationBus>().navigateToInvoice(invoiceId)
            }
            return
        }

        if (type.equals("MAINTENANCE", ignoreCase = true) || type.equals("WATER_CUT", ignoreCase = true)) {
            showMaintenanceNotification(title = title, body = body)
            return
        }

        if (type.equals("NOTIFICATION", ignoreCase = true) || type.equals("FEATURED", ignoreCase = true)) {
            showGeneralNewsNotification(title = title, body = body)
            return
        }

        if (body.isBlank() && url.isBlank()) return

        showNotification(title = title, body = body, url = url)
    }

    private fun showNotification(title: String, body: String, url: String) {
        val channelId = "NEW_POST"
        ensureChannel(channelId, name = "Bài viết mới")

        val contentIntent = if (url.isNotBlank()) {
            val intent = Intent(this, WebViewActivity::class.java).apply {
                putExtra(WebViewActivity.EXTRA_URL, url)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            val flags = (PendingIntent.FLAG_UPDATE_CURRENT or
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
            PendingIntent.getActivity(this, url.hashCode(), intent, flags)
        } else {
            null
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body.ifBlank { url })
            .setStyle(NotificationCompat.BigTextStyle().bigText(body.ifBlank { url }))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()

        NotificationManagerCompat.from(this).notify((body + url).hashCode(), notification)
    }

    private fun showArticleNotification(
        title: String,
        body: String,
        articleTitle: String,
        articleContent: String,
    ) {
        val channelId = "ARTICLE_NEWS"
        ensureChannel(channelId, name = "Bài viết nổi bật")

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("article_title", articleTitle)
            putExtra("article_content", articleContent)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val flags = (PendingIntent.FLAG_UPDATE_CURRENT or
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        val contentIntent = PendingIntent.getActivity(this, articleTitle.hashCode(), intent, flags)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()

        NotificationManagerCompat.from(this).notify(articleTitle.hashCode(), notification)
    }

    // Spec phananh_reply.md §1: FEEDBACK push → navigate FeedbackDetailScreen(id)
    private fun showFeedbackNotification(title: String, body: String, feedbackId: Long) {
        val channelId = "FEEDBACK_UPDATE"
        ensureChannel(channelId, name = "Cập nhật phản ánh")

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("feedback_id", feedbackId)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val flags = (PendingIntent.FLAG_UPDATE_CURRENT or
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        val requestCode = System.currentTimeMillis().toInt()
        val contentIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            flags
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()

        NotificationManagerCompat.from(this).notify(requestCode, notification)
    }

    // Hóa đơn mới / Thanh toán thành công → mở màn hình Danh sách Hóa đơn hoặc Chi tiết hóa đơn
    private fun showInvoiceNotification(title: String, body: String, invoiceId: Long? = null) {
        val channelId = "INVOICE_BILLING"
        ensureChannel(channelId, name = "Hóa đơn tiền nước")

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("navigate_to", "notifications_billing")
            if (invoiceId != null) {
                putExtra("invoice_id", invoiceId)
            }
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val flags = (PendingIntent.FLAG_UPDATE_CURRENT or
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        val requestCode = System.currentTimeMillis().toInt()
        val contentIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            flags
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()

        NotificationManagerCompat.from(this).notify(requestCode, notification)
    }

    // type=MAINTENANCE → mở tab Cúp nước trong màn Thông báo
    private fun showMaintenanceNotification(title: String, body: String) {
        val channelId = "MAINTENANCE_NEWS"
        ensureChannel(channelId, name = "Cúp nước / Bảo trì")

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("navigate_to", "notifications_maintenance")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val flags = (PendingIntent.FLAG_UPDATE_CURRENT or
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        val requestCode = System.currentTimeMillis().toInt()
        val contentIntent = PendingIntent.getActivity(this, requestCode, intent, flags)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()

        NotificationManagerCompat.from(this).notify(requestCode, notification)
    }

    // type=NOTIFICATION → mở tab Nổi bật trong màn Thông báo
    private fun showGeneralNewsNotification(title: String, body: String) {
        val channelId = "GENERAL_NEWS"
        ensureChannel(channelId, name = "Tin nổi bật")

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("navigate_to", "notifications_featured")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val flags = (PendingIntent.FLAG_UPDATE_CURRENT or
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        val requestCode = System.currentTimeMillis().toInt()
        val contentIntent = PendingIntent.getActivity(this, requestCode, intent, flags)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()

        NotificationManagerCompat.from(this).notify(requestCode, notification)
    }

    private fun ensureChannel(channelId: String, name: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val existing = manager.getNotificationChannel(channelId)
        if (existing != null) return

        val channel = NotificationChannel(
            channelId,
            name,
            NotificationManager.IMPORTANCE_HIGH
        )
        manager.createNotificationChannel(channel)
    }
}
