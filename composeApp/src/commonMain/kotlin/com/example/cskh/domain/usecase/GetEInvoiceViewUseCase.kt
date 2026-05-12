package com.example.cskh.domain.usecase

import com.example.cskh.domain.model.EInvoiceData
import com.example.cskh.domain.repository.InvoiceRepository

class GetEInvoiceViewUseCase(
    private val invoiceRepository: InvoiceRepository,
) {
    suspend operator fun invoke(baseUrl: String, id: Long): Result<EInvoiceData> =
        invoiceRepository.viewEInvoice(baseUrl, id)
}
