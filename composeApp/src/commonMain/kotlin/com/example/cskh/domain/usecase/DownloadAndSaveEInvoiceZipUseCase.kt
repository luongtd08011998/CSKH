package com.example.cskh.domain.usecase

import com.example.cskh.domain.repository.InvoiceRepository
import com.example.cskh.platform.InvoiceZipSaver

class DownloadAndSaveEInvoiceZipUseCase(
    private val invoiceRepository: InvoiceRepository,
    private val zipSaver: InvoiceZipSaver,
) {
    suspend operator fun invoke(baseUrl: String, invoiceId: Long): Result<String> {
        val bytes = invoiceRepository.downloadEInvoiceZip(baseUrl, invoiceId).getOrElse { return Result.failure(it) }
        return zipSaver.saveEInvoiceZip(invoiceId, bytes)
    }
}
