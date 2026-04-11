package com.example.cskh.domain.preferences

interface UserFormStore {
    fun loadDigiCode(): String
    fun loadPhone(): String
    fun loadBaseUrl(): String
    fun save(digiCode: String, phone: String, baseUrl: String)
}
