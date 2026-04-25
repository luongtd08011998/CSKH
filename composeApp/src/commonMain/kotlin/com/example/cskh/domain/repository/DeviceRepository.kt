package com.example.cskh.domain.repository

interface DeviceRepository {

    suspend fun registerDevice(
        baseUrl: String,
        deviceToken: String,
        accessToken: String,
    ): Result<Unit>
}
