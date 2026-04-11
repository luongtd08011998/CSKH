package com.example.cskh.domain.model

data class CustomerProfile(
    val digiCode: String,
    val name: String,
    val address: String,
    val phone: String,
    val email: String,
    val sms: String,
    val taxCode: String,
    val isActive: Int,
    val isWaterCut: Int,
)
