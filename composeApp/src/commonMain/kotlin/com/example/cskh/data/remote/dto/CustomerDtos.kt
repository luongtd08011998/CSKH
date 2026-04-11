package com.example.cskh.data.remote.dto

import com.example.cskh.domain.model.CustomerProfile
import kotlinx.serialization.Serializable

@Serializable
data class CustomerMeDataDto(
    val digiCode: String = "",
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String? = null,
    val sms: String? = null,
    val taxCode: String? = null,
    val isActive: Int = 0,
    val isWaterCut: Int = 0,
)

@Serializable
data class CustomerMeResponseDto(
    val data: CustomerMeDataDto? = null,
    val message: String? = null,
    val statusCode: Int? = null,
)

fun CustomerMeDataDto.toDomain(): CustomerProfile = CustomerProfile(
    digiCode = digiCode,
    name = name,
    address = address,
    phone = phone,
    email = email.orEmpty(),
    sms = sms.orEmpty(),
    taxCode = taxCode.orEmpty(),
    isActive = isActive,
    isWaterCut = isWaterCut,
)
