package com.example.cskh.domain.usecase

import com.example.cskh.domain.repository.AuthRepository

/**
 * Làm mới accessToken.
 * Trả về Pair(newAccessToken, refreshToken).
 * Ném lỗi với message "REFRESH_EXPIRED" nếu refreshToken đã hết hạn (401).
 */
class RefreshTokenUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(
        baseUrl: String,
        refreshToken: String,
    ): Result<Pair<String, String>> = authRepository.refresh(baseUrl, refreshToken)
}
