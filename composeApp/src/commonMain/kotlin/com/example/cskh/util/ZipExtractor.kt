package com.example.cskh.util

import okio.Buffer
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
     * Ghi [zipBytes] vào [tempZipPath], duyệt mọi file (không phải thư mục),
     * gọi [onEachFile] với đường dẫn đã sanitize và nội dung.
     * Xoá file zip tạm khi xong.
     *
     * **Lỗi size-mismatch trong header ZIP** (server sinh sai metadata) bị nuốt
     * để không làm crash toàn bộ quá trình giải nén.
     */
    fun extractAll(
        zipBytes: ByteArray,
        tempZipPath: Path,
        onEachFile: (zipEntryPath: String, content: ByteArray) -> Unit,
    ) {
        // Sửa lại header bị sai từ server trước khi đưa cho Okio đọc!
        patchZipLocalFileHeaders(zipBytes)

        val fs = FileSystem.SYSTEM
        if (fs.exists(tempZipPath)) {
            fs.delete(tempZipPath)
        }
        fs.write(tempZipPath, mustCreate = true) {
            write(zipBytes)
        }
        val zipFs = try { fs.openZip(tempZipPath) } catch (_: Throwable) {
            runCatching { fs.delete(tempZipPath) }
            return
        }
        try {
            val paths: List<okio.Path> = try {
                zipFs.listRecursively("/".toPath())
                    .filter { p ->
                        val m = zipFs.metadataOrNull(p) ?: return@filter false
                        !m.isDirectory
                    }
                    .sortedBy { it.toString() }
                    .toList()
            } catch (_: Throwable) { emptyList() }

            for (p in paths) {
                val raw = p.toString().trimStart('/')
                val safe = sanitizeZipEntryPath(raw)

                // Đọc bằng Source + Buffer; bỏ qua lỗi
                // "expected N bytes but got M" do server sinh sai header ZIP.
                val bytes: ByteArray? = try {
                    val buf = Buffer()
                    val src = zipFs.source(p)
                    try {
                        buf.writeAll(src)
                    } catch (_: Throwable) {
                        // Server thường khai sai kích thước trong header ZIP.
                        // Bắt lỗi ở đây để giữ lại phần bytes đã đọc được trong buf.
                    } finally {
                        runCatching { src.close() }
                    }
                    if (buf.size > 0) buf.readByteArray() else null
                } catch (_: Throwable) { null }
                if (bytes == null) continue

                onEachFile(safe, bytes)
            }
        } finally {
            runCatching { zipFs.close() }
        }
        runCatching { fs.delete(tempZipPath) }
    }
}
