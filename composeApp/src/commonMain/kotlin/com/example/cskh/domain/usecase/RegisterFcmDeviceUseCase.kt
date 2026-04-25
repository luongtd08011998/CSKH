package com.example.cskh.domain.usecase

import com.example.cskh.data.session.SessionManager
import com.example.cskh.domain.repository.DeviceRepository

class RegisterFcmDeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val sessionManager: SessionManager,
) {

    suspend fun register(baseUrl: String, deviceToken: String): Result<Unit> {
        val access = sessionManager.accessToken?.takeIf { it.isNotBlank() }
            ?: return Result.failure(IllegalStateException("Chưa đăng nhập"))
        return deviceRepository.registerDevice(baseUrl, deviceToken, access)
    }
}
