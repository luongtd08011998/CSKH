package com.example.cskh.domain.usecase

import com.example.cskh.domain.preferences.UserFormStore

class UserFormPreferencesUseCase(private val store: UserFormStore) {
    fun getDigiCode(): String = store.loadDigiCode()
    fun getPhone(): String = store.loadPhone()
    fun getBaseUrl(): String = store.loadBaseUrl()
    fun getAccessToken(): String = store.loadAccessToken()
    fun getRefreshToken(): String = store.loadRefreshToken()
    fun isNotificationPermissionPrompted(): Boolean = store.loadNotificationPermissionPrompted()

    fun saveForm(digiCode: String, phone: String, baseUrl: String) {
        store.save(digiCode, phone, baseUrl)
    }

    fun saveAccessToken(token: String) = store.saveAccessToken(token)
    fun saveRefreshToken(token: String) = store.saveRefreshToken(token)
    fun clearTokens() {
        store.clearAccessToken()
        store.clearRefreshToken()
    }
    fun setNotificationPermissionPrompted(value: Boolean) = store.saveNotificationPermissionPrompted(value)

    // back-compat
    fun clearAccessToken() = store.clearAccessToken()
}
