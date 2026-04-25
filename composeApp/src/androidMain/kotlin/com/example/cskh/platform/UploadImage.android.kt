package com.example.cskh.platform

import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import kotlin.math.max

actual fun prepareUploadImage(image: PickedImage): UploadImage {
    val context = AndroidApplicationHolder.application
    val parsed = Uri.parse(image.uri)

    // First: try to read original bytes (fast path for JPEG from camera / cache).
    val originalBytes = context.contentResolver.openInputStream(parsed)?.use { it.readBytes() }
        ?: error("Không đọc được ảnh")

    // If it's already JPEG, keep as-is.
    if (isJpeg(originalBytes)) {
        val filename = ensureExt(image.name, "jpg")
        return UploadImage(
            bytes = originalBytes,
            filename = filename,
            contentType = io.ktor.http.ContentType.Image.JPEG,
        )
    }

    // Otherwise decode + recompress to JPEG for maximum backend compatibility.
    val bmp = BitmapFactory.decodeByteArray(originalBytes, 0, originalBytes.size)
        ?: error("Ảnh không hợp lệ")

    val out = ByteArrayOutputStream(max(8 * 1024, originalBytes.size))
    // Use quality 92 (good balance); backend usually accepts standard JPEG.
    val ok = bmp.compress(android.graphics.Bitmap.CompressFormat.JPEG, 92, out)
    if (!ok) error("Không chuyển đổi được ảnh")

    val jpegBytes = out.toByteArray()
    val filename = ensureExt(image.name, "jpg")
    return UploadImage(
        bytes = jpegBytes,
        filename = filename,
        contentType = io.ktor.http.ContentType.Image.JPEG,
    )
}

private fun isJpeg(bytes: ByteArray): Boolean {
    return bytes.size >= 3 &&
        (bytes[0].toInt() and 0xFF) == 0xFF &&
        (bytes[1].toInt() and 0xFF) == 0xD8 &&
        (bytes[2].toInt() and 0xFF) == 0xFF
}

private fun ensureExt(name: String, ext: String): String {
    val base = name.substringBeforeLast('.', name)
    return "$base.$ext"
}

