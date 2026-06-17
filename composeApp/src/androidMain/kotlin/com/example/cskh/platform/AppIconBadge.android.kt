package com.example.cskh.platform

/** Android không cần set badge thủ công — FCM/notification channel tự xử lý. */
actual fun createAppIconBadge(): AppIconBadge = object : AppIconBadge {
    override fun setCount(count: Int) = Unit // no-op
}
