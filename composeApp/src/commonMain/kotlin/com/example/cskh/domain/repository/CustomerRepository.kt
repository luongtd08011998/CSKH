package com.example.cskh.domain.repository

import com.example.cskh.domain.model.CustomerDocument
import com.example.cskh.domain.model.CustomerProfile

interface CustomerRepository {
    suspend fun getMe(baseUrl: String): Result<CustomerProfile>
    suspend fun getDocumentList(code: String, phone: String): Result<List<CustomerDocument>>
    suspend fun downloadDocument(fullname: String): Result<ByteArray>
}
