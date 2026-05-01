package com.example.cskh.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val digiCode: String,
    val phone: String,
)

@Serializable
data class RefreshRequestDto(
    val refreshToken: String,
)

@Serializable
data class AuthDataDto(
    val accessToken: String,
    val refreshToken: String,
)

@Serializable
data class AuthResponseDto(
    val data: AuthDataDto? = null,
    val message: String? = null,
    val statusCode: Int? = null,
)

// ── back-compat aliases ──────────────────────────────────────────────────────
typealias LoginDataDto = AuthDataDto
typealias LoginResponseDto = AuthResponseDto
