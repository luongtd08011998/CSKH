package com.example.cskh.domain.repository

interface AuthRepository {
    /** Đăng nhập, trả về Pair(accessToken, refreshToken) */
    suspend fun login(baseUrl: String, digiCode: String, phone: String): Result<Pair<String, String>>

    /**
     * Lấy accessToken mới từ refreshToken.
     * @return Pair(newAccessToken, refreshToken) – refreshToken giữ nguyên theo spec.
     * Trả về lỗi nếu refreshToken đã hết hạn (server trả 401).
     */
    suspend fun refresh(baseUrl: String, refreshToken: String): Result<Pair<String, String>>

    /** Đăng xuất – server xóa tất cả refreshToken của KH */
    suspend fun logout(baseUrl: String): Result<Unit>
}
