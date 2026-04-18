package com.example.cskh.platform

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.InternalAPI
import io.ktor.client.statement.readRawBytes
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fwrite
import kotlin.random.Random

private val qrDownloadClient by lazy {
    HttpClient(Darwin) {
        install(HttpTimeout) {
            requestTimeoutMillis = 60_000
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 60_000
        }
    }
}

actual class QrPngSaverImpl actual constructor() : QrPngSaver {

    @OptIn(InternalAPI::class)
    override suspend fun savePngFromUrl(url: String): Result<String> = withContext(Dispatchers.Default) {
        runCatching {
            val response = qrDownloadClient.get(url) {
                header(HttpHeaders.Accept, "image/png, image/*, */*")
                header(HttpHeaders.AcceptEncoding, "identity")
            }
            if (response.status.value !in 200..299) {
                val text = runCatching { response.bodyAsText() }.getOrNull()
                error(text ?: "HTTP ${response.status.value}")
            }
            val bytes = response.readRawBytes()
            if (bytes.isEmpty()) error("Ảnh tải về trống")
            val name = "vietqr_${Random.nextLong()}.png"
            val path = writePngToDocumentsDownloads(name, bytes)
            "Đã lưu: $name. Mở Files › On My iPhone › Downloads.\n$path"
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun writePngToDocumentsDownloads(fileName: String, bytes: ByteArray): String {
    val fm = NSFileManager.defaultManager
    val docUrl = fm.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    ) ?: error("Không tìm được thư mục Documents")
    val basePath = docUrl.path ?: error("Đường dẫn không hợp lệ")
    val downloadDir = "$basePath/Downloads"
    if (!fm.fileExistsAtPath(downloadDir)) {
        fm.createDirectoryAtPath(downloadDir, withIntermediateDirectories = true, attributes = null, error = null)
    }
    val fullPath = "$downloadDir/$fileName"
    writeBytesToPath(fullPath, bytes)
    return fullPath
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
            if (written != count) error("Ghi file không đủ dung lượng")
        } finally {
            fclose(f)
        }
    }
}
