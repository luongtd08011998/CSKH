package com.example.cskh.platform

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.cskh.util.ZipExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.Path
import okio.Path.Companion.toPath
import java.io.File
import java.io.FileOutputStream

actual class InvoiceZipSaverImpl actual constructor() : InvoiceZipSaver {

    override suspend fun saveEInvoiceZip(invoiceId: Long, bytes: ByteArray): Result<String> = withContext(Dispatchers.IO) {
        // Bước 1: lưu file ZIP — bắt buộc phải thành công.
        val saved = runCatching {
            val ctx = AndroidApplicationHolder.application
            saveZipToDownloads(ctx, invoiceId, bytes)
        }.getOrElse { return@withContext Result.failure(it) }

        // Bước 2: giải nén — best-effort, không làm thất bại toàn bộ luồng.
        runCatching {
            val ctx = AndroidApplicationHolder.application
            val tempZip = invoiceTempZipAbsolutePath(invoiceId).toPath()
            extractZipToCurrentDownloads(ctx, invoiceId, bytes, tempZip)
        }

        Result.success(buildSuccessMessage(invoiceId, saved))
    }
}

private data class SavedZipInfo(
    val zipFileName: String,
    /** content://… trên Android 10+, hoặc đường dẫn tuyệt đối trên máy cũ */
    val zipLocation: String,
)

private fun buildSuccessMessage(invoiceId: Long, saved: SavedZipInfo): String = buildString {
    appendLine("Tên file: ${saved.zipFileName}")
    append("Mở ứng dụng Files › Download để xem.")
}.trim()

private fun extractZipToCurrentDownloads(ctx: Context, invoiceId: Long, bytes: ByteArray, tempZip: Path) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ZipExtractor.extractAll(bytes, tempZip) { rel, data ->
            saveExtractedFileMediaQ(ctx, invoiceId, rel, data)
        }
    } else {
        @Suppress("DEPRECATION")
        val root = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "e_invoice_${invoiceId}_extracted",
        )
        root.mkdirs()
        ZipExtractor.extractAll(bytes, tempZip) { rel, data ->
            val target = File(root, rel)
            target.parentFile?.mkdirs()
            FileOutputStream(target).use { it.write(data) }
        }
    }
}

private fun saveExtractedFileMediaQ(ctx: Context, invoiceId: Long, safeRelativePath: String, fileBytes: ByteArray) {
    val folder = "${Environment.DIRECTORY_DOWNLOADS}/e_invoice_${invoiceId}_extracted"
    val displayName = safeRelativePath.replace('/', '_').ifBlank { "file.bin" }
    val resolver = ctx.contentResolver
    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        put(MediaStore.MediaColumns.MIME_TYPE, mimeForExtractedName(displayName))
        put(MediaStore.MediaColumns.RELATIVE_PATH, folder)
        put(MediaStore.MediaColumns.IS_PENDING, 1)
    }
    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
        ?: error("Không tạo được file: $displayName")
    resolver.openOutputStream(uri, "w")?.use { it.write(fileBytes) }
        ?: error("Không ghi được: $displayName")
    values.clear()
    values.put(MediaStore.MediaColumns.IS_PENDING, 0)
    resolver.update(uri, values, null, null)
}

private fun mimeForExtractedName(name: String): String = when {
    name.endsWith(".html", ignoreCase = true) -> "text/html"
    name.endsWith(".htm", ignoreCase = true) -> "text/html"
    name.endsWith(".xml", ignoreCase = true) -> "application/xml"
    name.endsWith(".pdf", ignoreCase = true) -> "application/pdf"
    name.endsWith(".png", ignoreCase = true) -> "image/png"
    name.endsWith(".jpg", ignoreCase = true) || name.endsWith(".jpeg", ignoreCase = true) -> "image/jpeg"
    name.endsWith(".json", ignoreCase = true) -> "application/json"
    else -> "application/octet-stream"
}

private fun saveZipToDownloads(ctx: Context, invoiceId: Long, fileBytes: ByteArray): SavedZipInfo {
    val baseName = "e_invoice_$invoiceId.zip"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val resolver = ctx.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, baseName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/zip")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: error("Không thể tạo file trong thư mục Tải xuống")
        resolver.openOutputStream(uri, "w")?.use { it.write(fileBytes) }
            ?: error("Không ghi được dữ liệu zip")
        values.clear()
        values.put(MediaStore.MediaColumns.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
        return SavedZipInfo(zipFileName = baseName, zipLocation = uri.toString())
    } else {
        @Suppress("DEPRECATION")
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!dir.exists()) dir.mkdirs()
        var file = File(dir, baseName)
        if (file.exists()) {
            val stem = "e_invoice_${invoiceId}_${System.currentTimeMillis()}"
            file = File(dir, "$stem.zip")
        }
        FileOutputStream(file).use { it.write(fileBytes) }
        return SavedZipInfo(zipFileName = file.name, zipLocation = file.absolutePath)
    }
}
