package com.example.cskh.domain.usecase

import com.example.cskh.data.session.SessionManager
import com.example.cskh.domain.repository.DeviceRepository

class UnregisterFcmDeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val sessionManager: SessionManager,
) {

    suspend fun unregister(baseUrl: String, deviceToken: String): Result<Unit> {
        val access = sessionManager.accessToken?.takeIf { it.isNotBlank() }
            ?: return Result.failure(IllegalStateException("Chưa đăng nhập"))
        return deviceRepository.unregisterDevice(baseUrl, deviceToken, access)
    }
}

