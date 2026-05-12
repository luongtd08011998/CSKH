package com.example.cskh.domain.repository

import com.example.cskh.domain.model.EInvoiceData
import com.example.cskh.domain.model.InvoiceDetail
import com.example.cskh.domain.model.PagedInvoices

interface InvoiceRepository {
    suspend fun getInvoices(baseUrl: String, page: Int, pageSize: Int): Result<PagedInvoices>
    suspend fun getInvoiceDetail(baseUrl: String, id: Long): Result<InvoiceDetail>
    suspend fun downloadEInvoiceZip(baseUrl: String, id: Long): Result<ByteArray>
    suspend fun viewEInvoice(baseUrl: String, id: Long): Result<EInvoiceData>
}
