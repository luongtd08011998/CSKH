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

actual fun createBinaryGetDownloader(): BinaryGetDownloader = IosBinaryGetDownloader()

// Client riêng, KHÔNG gắn ContentNegotiation và bật timeout.
private val downloadClient by lazy {
    HttpClient(Darwin) {
        install(HttpTimeout) {
            requestTimeoutMillis = 120_000
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 120_000
        }
    }
}

@OptIn(InternalAPI::class)
private class IosBinaryGetDownloader : BinaryGetDownloader {
    override suspend fun getBytes(url: String, bearerToken: String): ByteArray {
        val response = downloadClient.get(url) {
            header(HttpHeaders.Authorization, "Bearer $bearerToken")
            header(HttpHeaders.Accept, "application/zip, application/octet-stream, */*")
            // Ép server không nén: NSURLSession sẽ không tự gzip; Content-Length sẽ khớp thực tế.
            header(HttpHeaders.AcceptEncoding, "identity")
        }
        if (response.status.value !in 200..299) {
            val text = runCatching { response.bodyAsText() }.getOrNull()
            error(text ?: "HTTP ${response.status.value}")
        }
        val bytes = response.readRawBytes()
        if (bytes.isEmpty()) error("Nhận được phản hồi trống từ server")
        return bytes
    }
}
