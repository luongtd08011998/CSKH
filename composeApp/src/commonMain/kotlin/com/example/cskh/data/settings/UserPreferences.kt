package com.example.cskh.data.settings

import com.example.cskh.domain.preferences.UserFormStore
import com.russhwolf.settings.Settings

class UserPreferences(private val settings: Settings) : UserFormStore {

    override fun loadDigiCode(): String = settings.getString(KEY_DIGI, "")
    override fun loadPhone(): String = settings.getString(KEY_PHONE, "")
    override fun loadBaseUrl(): String = settings.getString(KEY_BASE, "")

    override fun loadAccessToken(): String = settings.getString(KEY_ACCESS, "")
    override fun loadRefreshToken(): String = settings.getString(KEY_REFRESH, "")
    override fun loadNotificationPermissionPrompted(): Boolean = settings.getBoolean(KEY_NOTIF_PROMPTED, false)

    override fun save(digiCode: String, phone: String, baseUrl: String) {
        settings.putString(KEY_DIGI, digiCode)
        settings.putString(KEY_PHONE, phone)
        settings.putString(KEY_BASE, baseUrl)
    }

    override fun saveAccessToken(token: String) {
        settings.putString(KEY_ACCESS, token)
    }

    override fun saveRefreshToken(token: String) {
        settings.putString(KEY_REFRESH, token)
    }

    override fun saveNotificationPermissionPrompted(value: Boolean) {
        settings.putBoolean(KEY_NOTIF_PROMPTED, value)
    }

    override fun clearAccessToken() {
        settings.putString(KEY_ACCESS, "")
    }

    override fun clearRefreshToken() {
        settings.putString(KEY_REFRESH, "")
    }

    companion object {
        private const val KEY_DIGI = "pref_digi_code"
        private const val KEY_PHONE = "pref_phone"
        private const val KEY_BASE = "pref_base_url"
        private const val KEY_ACCESS = "pref_access_token"
        private const val KEY_REFRESH = "pref_refresh_token"
        private const val KEY_NOTIF_PROMPTED = "pref_notification_permission_prompted"
    }
}
