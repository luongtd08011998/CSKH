package com.example.cskh.domain.usecase

import com.example.cskh.domain.model.InvoiceDetail
import com.example.cskh.domain.repository.InvoiceRepository

class GetInvoiceDetailUseCase(private val invoiceRepository: InvoiceRepository) {
    suspend operator fun invoke(baseUrl: String, id: Long): Result<InvoiceDetail> =
        invoiceRepository.getInvoiceDetail(baseUrl, id)
}
