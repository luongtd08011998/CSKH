package com.example.cskh.domain.repository

interface RegisterRepository {
    suspend fun submitRegistration(
        name: String,
        phone: String,
        waterMeterAddress: String,
        email: String,
    ): Result<String>

    suspend fun getRegistrationTime(phone: String): Result<String>
}
