package com.example.cskh.domain.repository

interface AuthRepository {
    suspend fun login(baseUrl: String, digiCode: String, phone: String): Result<String>
}
