package com.example.cskh.util

/**
 * Vá lỗi file ZIP bị sai thông tin kích thước ở Local File Header.
 * Server đôi khi ghi kích thước sai vào Local File Header (ví dụ 112946 thay vì 14029),
 * nhưng lại ghi đúng ở Central Directory. Điều này khiến Okio ZipFS bị crash.
 * 
 * Hàm này sẽ đọc Central Directory, lấy thông tin CRC, Compressed Size,
 * Uncompressed Size đúng, và ghi đè ngược lại vào Local File Header.
 */
fun patchZipLocalFileHeaders(zipBytes: ByteArray) {
    fun le32(off: Int): Int =
        (zipBytes[off].toInt() and 0xFF) or
        ((zipBytes[off + 1].toInt() and 0xFF) shl 8) or
        ((zipBytes[off + 2].toInt() and 0xFF) shl 16) or
        ((zipBytes[off + 3].toInt() and 0xFF) shl 24)

    // Tìm End of Central Directory
    var eocd = -1
    for (i in zipBytes.size - 22 downTo maxOf(0, zipBytes.size - 65558)) {
        if (zipBytes[i] == 0x50.toByte() && zipBytes[i + 1] == 0x4B.toByte() &&
            zipBytes[i + 2] == 0x05.toByte() && zipBytes[i + 3] == 0x06.toByte()) {
            eocd = i
            break
        }
    }
    if (eocd < 0) return

    val cdOffset = le32(eocd + 16)
    val cdSize = le32(eocd + 12)
    if (cdOffset < 0 || cdOffset + cdSize > zipBytes.size) return

    var pos = cdOffset
    while (pos + 46 <= cdOffset + cdSize && pos + 46 <= zipBytes.size) {
        if (zipBytes[pos] != 0x50.toByte() || zipBytes[pos + 1] != 0x4B.toByte() ||
            zipBytes[pos + 2] != 0x01.toByte() || zipBytes[pos + 3] != 0x02.toByte()) break

        val nameLen = (zipBytes[pos + 28].toInt() and 0xFF) or ((zipBytes[pos + 29].toInt() and 0xFF) shl 8)
        val extraLen = (zipBytes[pos + 30].toInt() and 0xFF) or ((zipBytes[pos + 31].toInt() and 0xFF) shl 8)
        val commentLen = (zipBytes[pos + 32].toInt() and 0xFF) or ((zipBytes[pos + 33].toInt() and 0xFF) shl 8)
        val localOffset = le32(pos + 42)

        // Copy 12 bytes (CRC, compSize, uncompSize) từ CD (offset 16) sang Local Header (offset 14)
        if (localOffset >= 0 && localOffset + 30 <= zipBytes.size) {
            if (zipBytes[localOffset] == 0x50.toByte() && zipBytes[localOffset + 1] == 0x4B.toByte() &&
                zipBytes[localOffset + 2] == 0x03.toByte() && zipBytes[localOffset + 3] == 0x04.toByte()) {
                
                // Copy CRC, Compressed Size, Uncompressed Size
                for (i in 0 until 12) {
                    zipBytes[localOffset + 14 + i] = zipBytes[pos + 16 + i]
                }
            }
        }

        pos += 46 + nameLen + extraLen + commentLen
    }
}
