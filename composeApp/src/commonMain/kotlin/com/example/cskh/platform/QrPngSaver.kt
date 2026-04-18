package com.example.cskh.platform

interface QrPngSaver {
    /** Tải ảnh PNG từ URL công khai (không Authorization) và lưu vào thư mục tải xuống của thiết bị. */
    suspend fun savePngFromUrl(url: String): Result<String>
}

expect class QrPngSaverImpl() : QrPngSaver
