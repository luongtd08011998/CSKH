package com.example.cskh.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    val name: String,
    val phone: String,
    val waterMeterAddress: String,
    val email: String,
)

@Serializable
data class RegisterResponseDto(
    val retCode: String? = null,
    val retMsg: String? = null,
)
