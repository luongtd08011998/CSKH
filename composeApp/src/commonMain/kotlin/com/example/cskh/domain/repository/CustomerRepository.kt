package com.example.cskh.domain.repository

import com.example.cskh.domain.model.CustomerProfile

interface CustomerRepository {
    suspend fun getMe(baseUrl: String): Result<CustomerProfile>
}
