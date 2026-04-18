package com.example.cskh.platform

/**
 * GET nhị phân đơn giản (Authorization), đọc đến hết luồng — tránh lệch Content-Length với Ktor body pipeline.
 */
interface BinaryGetDownloader {
    suspend fun getBytes(url: String, bearerToken: String): ByteArray
}

expect fun createBinaryGetDownloader(): BinaryGetDownloader
