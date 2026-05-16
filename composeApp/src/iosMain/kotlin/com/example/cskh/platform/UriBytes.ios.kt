package com.example.cskh.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSURL
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fread
import platform.posix.fseek
import platform.posix.ftell

@OptIn(ExperimentalForeignApi::class)
actual fun readBytesFromUri(uri: String): ByteArray {
    val path = resolveLocalPath(uri)
    val f = fopen(path, "rb") ?: error("Không đọc được ảnh từ $path")
    try {
        fseek(f, 0, platform.posix.SEEK_END)
        val size = ftell(f).toInt()
        if (size <= 0) return ByteArray(0)
        fseek(f, 0, platform.posix.SEEK_SET)
        val out = ByteArray(size)
        out.usePinned { pinned ->
            fread(pinned.addressOf(0), 1u, size.toULong(), f)
        }
        return out
    } finally {
        fclose(f)
    }
}

private fun resolveLocalPath(uri: String): String {
    if (uri.startsWith("file://")) {
        return NSURL.URLWithString(uri)?.path ?: uri.removePrefix("file://")
    }
    return uri
}
