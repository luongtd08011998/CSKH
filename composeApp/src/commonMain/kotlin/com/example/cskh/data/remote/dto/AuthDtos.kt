package com.example.cskh.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val digiCode: String,
    val phone: String,
)

@Serializable
data class LoginDataDto(
    val accessToken: String,
)

@Serializable
data class LoginResponseDto(
    val data: LoginDataDto? = null,
    val message: String? = null,
    val statusCode: Int? = null,
)
