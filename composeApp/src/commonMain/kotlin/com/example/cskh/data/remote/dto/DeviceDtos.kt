package com.example.cskh.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DeviceRegisterRequestDto(
    val deviceToken: String,
    val platform: String = "ANDROID",
)
