package com.example.cskh.util

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.openZip

/**
 * Chuẩn hoá đường dẫn entry trong zip (tránh zip-slip, bỏ phần tử rỗng / `.`).
 */
fun sanitizeZipEntryPath(raw: String): String {
    val parts = raw.trim().trimStart('/').split('/', '\\')
        .filter { it.isNotBlank() && it != "." && it != ".." }
    return parts.joinToString("/").ifBlank { "file.bin" }
}

object ZipExtractor {
    /**
     * Ghi [zipBytes] vào [tempZipPath], duyệt mọi file (không phải thư mục), gọi [onEachFile] với đường dẫn đã sanitize và nội dung.
     * Xoá file zip tạm khi xong.
     */
    fun extractAll(
        zipBytes: ByteArray,
        tempZipPath: Path,
        onEachFile: (zipEntryPath: String, content: ByteArray) -> Unit,
    ) {
        val fs = FileSystem.SYSTEM
        if (fs.exists(tempZipPath)) {
            fs.delete(tempZipPath)
        }
        fs.write(tempZipPath, mustCreate = true) {
            write(zipBytes)
        }
        val zipFs = fs.openZip(tempZipPath)
        try {
            val paths = zipFs.listRecursively("/".toPath())
                .filter { p ->
                    val m = zipFs.metadataOrNull(p) ?: return@filter false
                    !m.isDirectory
                }
                .sortedBy { it.toString() }
                .toList()
            for (p in paths) {
                val raw = p.toString().trimStart('/')
                val safe = sanitizeZipEntryPath(raw)
                val bytes = zipFs.read(p) { readByteArray() }
                onEachFile(safe, bytes)
            }
        } finally {
            zipFs.close()
        }
        runCatching { fs.delete(tempZipPath) }
    }
}
