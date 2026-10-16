package com.example.cskh.domain.usecase

import com.example.cskh.domain.repository.CustomerRepository

class DownloadCustomerDocUseCase(private val customerRepository: CustomerRepository) {
    suspend operator fun invoke(fullname: String): Result<ByteArray> =
        customerRepository.downloadDocument(fullname)
}
