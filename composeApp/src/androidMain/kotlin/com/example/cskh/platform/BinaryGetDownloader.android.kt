package com.example.cskh.platform

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

actual fun createBinaryGetDownloader(): BinaryGetDownloader = AndroidBinaryGetDownloader()

private class AndroidBinaryGetDownloader : BinaryGetDownloader {
    override suspend fun getBytes(url: String, bearerToken: String): ByteArray = withContext(Dispatchers.IO) {
        val conn = URL(url).openConnection() as HttpURLConnection
        try {
            conn.requestMethod = "GET"
            conn.setRequestProperty("Authorization", "Bearer $bearerToken")
            conn.setRequestProperty("Accept", "application/zip, application/octet-stream, */*")
            // Tắt nén tự động: Java sẽ KHÔNG bọc FixedLengthInputStream theo Content-Length nén,
            // server phải gửi dữ liệu thô với Content-Length chính xác.
            conn.setRequestProperty("Accept-Encoding", "identity")
            conn.instanceFollowRedirects = true
            conn.connectTimeout = 30_000
            conn.readTimeout = 120_000
            conn.connect()

            val code = conn.responseCode
            if (code !in 200..299) {
                val err = conn.errorStream?.use { it.readBytes() }?.decodeToString()
                error(err?.takeIf { it.isNotBlank() } ?: "HTTP $code")
            }

            // Đọc đến EOF thật sự — ByteArrayOutputStream không giới hạn theo Content-Length.
            val out = ByteArrayOutputStream()
            val buf = ByteArray(8 * 1024)
            conn.inputStream.use { stream ->
                var n = stream.read(buf)
                while (n >= 0) {
                    out.write(buf, 0, n)
                    n = stream.read(buf)
                }
            }
            val bytes = out.toByteArray()
            if (bytes.isEmpty()) error("Nhận được phản hồi trống từ server")
            bytes
        } finally {
            conn.disconnect()
        }
    }
}
