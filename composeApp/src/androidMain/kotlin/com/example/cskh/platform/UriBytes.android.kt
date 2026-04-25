package com.example.cskh.platform

import android.net.Uri

actual fun readBytesFromUri(uri: String): ByteArray {
    val context = AndroidApplicationHolder.application
    val parsed = Uri.parse(uri)
    val input = context.contentResolver.openInputStream(parsed)
        ?: error("Không đọc được ảnh")
    return input.use { it.readBytes() }
}
