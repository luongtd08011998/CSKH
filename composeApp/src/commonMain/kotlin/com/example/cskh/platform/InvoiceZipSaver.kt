package com.example.cskh.platform

interface InvoiceZipSaver {
    suspend fun saveEInvoiceZip(invoiceId: Long, bytes: ByteArray): Result<String>
}

expect class InvoiceZipSaverImpl() : InvoiceZipSaver
