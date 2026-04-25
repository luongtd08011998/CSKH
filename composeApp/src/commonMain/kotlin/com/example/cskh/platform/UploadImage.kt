package com.example.cskh.platform

import io.ktor.http.ContentType

data class UploadImage(
    val bytes: ByteArray,
    val filename: String,
    val contentType: ContentType,
)

/**
 * Backend may be strict about accepted image formats.
 * This prepares a safe payload for upload (e.g. converts to JPEG on Android when needed).
 */
expect fun prepareUploadImage(image: PickedImage): UploadImage

