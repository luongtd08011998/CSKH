package com.example.cskh.platform

import com.example.cskh.util.ZipExtractor
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fwrite

@OptIn(ExperimentalForeignApi::class)
actual class InvoiceZipSaverImpl actual constructor() : InvoiceZipSaver {

    override suspend fun saveEInvoiceZip(invoiceId: Long, bytes: ByteArray): Result<String> = withContext(Dispatchers.Default) {
        // Bước 1: lưu file ZIP — bắt buộc thành công.
        val zipPath = try {
            val fm = NSFileManager.defaultManager
            val docUrl = fm.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = true,
                error = null,
            ) ?: return@withContext Result.failure(Exception("Không tìm được thư mục Documents"))
            val basePath = docUrl.path
                ?: return@withContext Result.failure(Exception("Đường dẫn không hợp lệ"))
            val downloadDir = "$basePath/Downloads"
            if (!fm.fileExistsAtPath(downloadDir)) {
                fm.createDirectoryAtPath(downloadDir, withIntermediateDirectories = true, attributes = null, error = null)
            }
            writeZipToDownloads(fm, downloadDir, invoiceId, bytes)
        } catch (e: Throwable) {
            return@withContext Result.failure(e)
        }

        // Bước 2: giải nén — best-effort, không làm thất bại toàn bộ luồng.
        runCatching {
            val fm = NSFileManager.defaultManager
            val extractRoot = "${zipPath.substringBeforeLast('/')}/e_invoice_${invoiceId}_extracted"
            if (!fm.fileExistsAtPath(extractRoot)) {
                fm.createDirectoryAtPath(extractRoot, withIntermediateDirectories = true, attributes = null, error = null)
            }
            val tempZip = invoiceTempZipAbsolutePath(invoiceId).toPath()
            ZipExtractor.extractAll(bytes, tempZip) { rel, data ->
                val fullPath = "$extractRoot/$rel"
                ensureParentDirExists(fm, fullPath)
                writeBytesToPath(fullPath, data)
            }
        }

        Result.success(
            buildString {
                appendLine("Tên file: ${zipPath.substringAfterLast('/')}")
                append("Mở ứng dụng Files › On My iPhone › Downloads để xem.")
            }.trim(),
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun ensureParentDirExists(fm: NSFileManager, filePath: String) {
    val parent = filePath.substringBeforeLast('/', "")
    if (parent.isEmpty() || fm.fileExistsAtPath(parent)) return
    fm.createDirectoryAtPath(parent, withIntermediateDirectories = true, attributes = null, error = null)
}

private fun writeZipToDownloads(fm: NSFileManager, downloadDir: String, invoiceId: Long, bytes: ByteArray): String {
    val fileName = "e_invoice_$invoiceId.zip"
    val fullPath = "$downloadDir/$fileName"
    val pathToWrite = if (fm.fileExistsAtPath(fullPath)) {
        "$downloadDir/e_invoice_${invoiceId}_${Random.nextLong()}.zip"
    } else {
        fullPath
    }
    writeBytesToPath(pathToWrite, bytes)
    return pathToWrite
}

@OptIn(ExperimentalForeignApi::class)
private fun writeBytesToPath(path: String, bytes: ByteArray) {
    if (bytes.isEmpty()) {
        val f = fopen(path, "wb") ?: error("Không mở được file")
        fclose(f)
        return
    }
    bytes.usePinned { pinned ->
        val f = fopen(path, "wb") ?: error("Không mở được file")
        try {
            val count = bytes.size.toULong()
            val written = fwrite(pinned.addressOf(0), 1u, count, f)
            if (written != count) {
                error("Ghi file không đủ dung lượng")
            }
        } finally {
            fclose(f)
        }
    }
}
