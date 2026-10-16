package com.example.cskh.domain.usecase

import com.example.cskh.domain.model.CustomerDocument
import com.example.cskh.domain.repository.CustomerRepository

class GetCustomerDocListUseCase(private val customerRepository: CustomerRepository) {
    suspend operator fun invoke(code: String, phone: String): Result<List<CustomerDocument>> =
        customerRepository.getDocumentList(code, phone)
}
