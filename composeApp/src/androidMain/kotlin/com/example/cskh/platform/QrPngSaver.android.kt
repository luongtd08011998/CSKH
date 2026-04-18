package com.example.cskh.platform

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

actual class QrPngSaverImpl actual constructor() : QrPngSaver {

    override suspend fun savePngFromUrl(url: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val bytes = downloadPublicPng(url)
            val name = "vietqr_${System.currentTimeMillis()}.png"
            val ctx = AndroidApplicationHolder.application
            val location = savePngToDownloads(ctx, name, bytes)
            "Đã lưu: $name. $location"
        }
    }
}

private fun downloadPublicPng(url: String): ByteArray {
    val conn = URL(url).openConnection() as HttpURLConnection
    try {
        conn.requestMethod = "GET"
        conn.setRequestProperty("Accept", "image/png, image/*, */*")
        conn.setRequestProperty("Accept-Encoding", "identity")
        conn.instanceFollowRedirects = true
        conn.connectTimeout = 30_000
        conn.readTimeout = 60_000
        conn.connect()
        val code = conn.responseCode
        if (code !in 200..299) {
            val err = conn.errorStream?.use { it.readBytes() }?.decodeToString()
            error(err?.takeIf { it.isNotBlank() } ?: "HTTP $code")
        }
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
        if (bytes.isEmpty()) error("Ảnh tải về trống")
        return bytes
    } finally {
        conn.disconnect()
    }
}

private fun savePngToDownloads(ctx: Context, displayName: String, fileBytes: ByteArray): String {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val resolver = ctx.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: error("Không thể tạo file trong thư mục Tải xuống")
        resolver.openOutputStream(uri, "w")?.use { it.write(fileBytes) }
            ?: error("Không ghi được ảnh")
        values.clear()
        values.put(MediaStore.MediaColumns.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
        return "Mở Files › Download."
    } else {
        @Suppress("DEPRECATION")
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, displayName)
        FileOutputStream(file).use { it.write(fileBytes) }
        return file.absolutePath
    }
}
