package com.example.cskh.platform

import io.ktor.http.ContentType

actual fun prepareUploadImage(image: PickedImage): UploadImage {
    val bytes = readBytesFromUri(image.uri)
    // iOS: keep original; backend compatibility issues have been observed on Android side.
    val ct = detectIosImageContentType(bytes) ?: guessByName(image.name)
    val filename = ensureFilenameMatches(image.name, ct)
    return UploadImage(bytes = bytes, filename = filename, contentType = ct)
}

private fun detectIosImageContentType(bytes: ByteArray): ContentType? {
    if (bytes.size < 12) return null
    // JPEG
    if ((bytes[0].toInt() and 0xFF) == 0xFF &&
        (bytes[1].toInt() and 0xFF) == 0xD8 &&
        (bytes[2].toInt() and 0xFF) == 0xFF
    ) return ContentType.Image.JPEG
    // PNG
    if (bytes[0] == 0x89.toByte() &&
        bytes[1] == 0x50.toByte() &&
        bytes[2] == 0x4E.toByte() &&
        bytes[3] == 0x47.toByte()
    ) return ContentType.Image.PNG
    // GIF
    if (bytes[0] == 'G'.code.toByte() &&
        bytes[1] == 'I'.code.toByte() &&
        bytes[2] == 'F'.code.toByte()
    ) return ContentType.Image.GIF
    // WebP
    if (bytes[0] == 'R'.code.toByte() &&
        bytes[1] == 'I'.code.toByte() &&
        bytes[2] == 'F'.code.toByte() &&
        bytes[3] == 'F'.code.toByte() &&
        bytes[8] == 'W'.code.toByte() &&
        bytes[9] == 'E'.code.toByte() &&
        bytes[10] == 'B'.code.toByte() &&
        bytes[11] == 'P'.code.toByte()
    ) return ContentType.parse("image/webp")
    return null
}

private fun guessByName(name: String): ContentType {
    val ext = name.substringAfterLast('.', "").lowercase()
    return when (ext) {
        "png" -> ContentType.Image.PNG
        "webp" -> ContentType.parse("image/webp")
        "gif" -> ContentType.Image.GIF
        else -> ContentType.Image.JPEG
    }
}

private fun ensureFilenameMatches(name: String, contentType: ContentType): String {
    val base = name.substringBeforeLast('.', name)
    val ext = when (contentType.toString()) {
        "image/png" -> "png"
        "image/gif" -> "gif"
        "image/webp" -> "webp"
        else -> "jpg"
    }
    return "$base.$ext"
}

