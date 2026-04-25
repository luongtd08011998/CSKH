package com.example.cskh.domain.usecase

import com.example.cskh.domain.preferences.UserFormStore

class UserFormPreferencesUseCase(private val store: UserFormStore) {
    fun getDigiCode(): String = store.loadDigiCode()
    fun getPhone(): String = store.loadPhone()
    fun getBaseUrl(): String = store.loadBaseUrl()
    fun getAccessToken(): String = store.loadAccessToken()

    fun saveForm(digiCode: String, phone: String, baseUrl: String) {
        store.save(digiCode, phone, baseUrl)
    }

    fun saveAccessToken(token: String) = store.saveAccessToken(token)
    fun clearAccessToken() = store.clearAccessToken()
}
