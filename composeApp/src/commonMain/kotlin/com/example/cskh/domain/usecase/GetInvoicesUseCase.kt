package com.example.cskh.domain.usecase

import com.example.cskh.domain.model.PagedInvoices
import com.example.cskh.domain.repository.InvoiceRepository

class GetInvoicesUseCase(private val invoiceRepository: InvoiceRepository) {
    suspend operator fun invoke(baseUrl: String, page: Int, pageSize: Int): Result<PagedInvoices> =
        invoiceRepository.getInvoices(baseUrl, page, pageSize)
}
