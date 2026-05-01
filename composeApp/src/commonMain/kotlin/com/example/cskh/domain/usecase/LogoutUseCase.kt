package com.example.cskh.domain.usecase

import com.example.cskh.domain.repository.AuthRepository

/** Gọi POST /auth/logout để server xóa tất cả refreshToken của KH */
class LogoutUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(baseUrl: String): Result<Unit> =
        authRepository.logout(baseUrl)
}
