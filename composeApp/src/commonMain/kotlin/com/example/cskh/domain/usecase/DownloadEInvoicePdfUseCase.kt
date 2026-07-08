package com.example.cskh.domain.usecase

import com.example.cskh.domain.repository.InvoiceRepository

class DownloadEInvoicePdfUseCase(
    private val invoiceRepository: InvoiceRepository,
) {
    suspend operator fun invoke(baseUrl: String, invoiceId: Long): Result<ByteArray> {
        return invoiceRepository.downloadEInvoicePdf(baseUrl, invoiceId)
    }
}
