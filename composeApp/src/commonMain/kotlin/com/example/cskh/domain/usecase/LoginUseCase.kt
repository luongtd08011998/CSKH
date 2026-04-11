package com.example.cskh.domain.usecase

import com.example.cskh.domain.repository.AuthRepository

class LoginUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(baseUrl: String, digiCode: String, phone: String): Result<String> =
        authRepository.login(baseUrl, digiCode, phone)
}
