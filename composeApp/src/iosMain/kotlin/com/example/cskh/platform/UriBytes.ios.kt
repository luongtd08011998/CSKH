package com.example.cskh.platform

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
actual fun readBytesFromUri(uri: String): ByteArray {
    val url = NSURL.URLWithString(uri) ?: NSURL.fileURLWithPath(uri)
    val data = NSData.dataWithContentsOfURL(url) ?: error("Không đọc được ảnh")
    val len = data.length.toInt()
    if (len <= 0) return ByteArray(0)
    val out = ByteArray(len)
    out.usePinned { pinned ->
        memcpy(
            pinned.addressOf(0),
            data.bytes as CPointer<*>?,
            data.length,
        )
    }
    return out
}

