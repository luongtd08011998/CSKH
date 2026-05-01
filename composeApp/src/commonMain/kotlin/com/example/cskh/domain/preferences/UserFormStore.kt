package com.example.cskh.domain.preferences

interface UserFormStore {
    fun loadDigiCode(): String
    fun loadPhone(): String
    fun loadBaseUrl(): String
    fun loadAccessToken(): String
    fun loadRefreshToken(): String
    fun loadNotificationPermissionPrompted(): Boolean
    fun save(digiCode: String, phone: String, baseUrl: String)
    fun saveAccessToken(token: String)
    fun saveRefreshToken(token: String)
    fun saveNotificationPermissionPrompted(value: Boolean)
    fun clearAccessToken()
    fun clearRefreshToken()
}
