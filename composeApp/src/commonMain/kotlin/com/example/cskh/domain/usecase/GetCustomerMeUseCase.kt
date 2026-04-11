package com.example.cskh.domain.usecase

import com.example.cskh.domain.model.CustomerProfile
import com.example.cskh.domain.repository.CustomerRepository

class GetCustomerMeUseCase(private val customerRepository: CustomerRepository) {
    suspend operator fun invoke(baseUrl: String): Result<CustomerProfile> =
        customerRepository.getMe(baseUrl)
}
