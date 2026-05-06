package com.example.cskh.data.session

import com.example.cskh.domain.usecase.LogoutUseCase
import com.example.cskh.domain.usecase.RefreshTokenUseCase
import com.example.cskh.domain.usecase.UserFormPreferencesUseCase
import com.example.cskh.platform.FcmDeviceSync
import com.example.cskh.platform.defaultDevMachineApiBaseUrl
import com.example.cskh.presentation.CompanyBranding
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Điều phối tập trung việc refresh token và logout.
 *
 * - [tryRefreshAndRetry]: gọi khi nhận 401 từ API bất kỳ.
 *   Nếu refresh thành công → cập nhật token và trả về true (caller nên retry request).
 *   Nếu refresh cũng 401 (hết hạn) → xóa session và trả về false (caller điều hướng về Login).
 *
 * - [logout]: gọi khi user bấm Đăng xuất – gọi API /auth/logout + xóa local session.
 */
class TokenRefreshCoordinator(
    private val sessionManager: SessionManager,
    private val formPreferences: UserFormPreferencesUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val fcmDeviceSync: FcmDeviceSync,
) {
    private val mutex = Mutex()

    /**
     * @return true nếu refresh thành công, false nếu cần điều hướng về Login.
     */
    suspend fun tryRefresh(): Boolean = mutex.withLock {
        val baseUrl = formPreferences.getBaseUrl()
        val currentRefresh = sessionManager.refreshToken ?: return@withLock false

        val result = refreshTokenUseCase(baseUrl, currentRefresh)
        return@withLock result.fold(
            onSuccess = { (newAccess, sameRefresh) ->
                sessionManager.setToken(newAccess, sameRefresh)
                formPreferences.saveAccessToken(newAccess)
                formPreferences.saveRefreshToken(sameRefresh)
                true
            },
            onFailure = {
                // Refresh hết hạn hoặc lỗi → xóa session local
                clearLocalSession()
                false
            },
        )
    }

    suspend fun logout(baseUrl: String) {
        runCatching { fcmDeviceSync.unregisterIfLoggedIn() }
            .onFailure { e ->
                println("[LOGOUT] unregister device failed: ${e.message}")
            }
        runCatching { logoutUseCase(baseUrl) }
            .onFailure { e ->
                println("[LOGOUT] logout API failed: ${e.message}")
            }
        clearLocalSession()
    }

    private fun clearLocalSession() {
        formPreferences.clearTokens()
        sessionManager.clear()
    }
}
