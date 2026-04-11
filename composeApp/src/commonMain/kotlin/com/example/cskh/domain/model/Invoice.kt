package com.example.cskh.domain.model

data class PageMeta(
    val page: Int,
    val pageSize: Int,
    val pages: Int,
    val total: Int,
)

data class InvoiceSummary(
    val id: Long,
    val digiCode: String,
    val customerName: String,
    val amount: Double,
    val envFee: Double,
    val taxFee: Double,
    val totalAmount: Double,
    val paymentStatus: Int,
    val paymentStatusLabel: String,
    val oldVal: Int,
    val newVal: Int,
)

data class PagedInvoices(
    val meta: PageMeta,
    val items: List<InvoiceSummary>,
)

data class InvoiceDetail(
    val id: Long,
    val digiCode: String?,
    val customerName: String?,
    val customerId: Long?,
    val yearMonth: String?,
    val amount: Double,
    val envFee: Double,
    val taxFee: Double,
    val totalAmount: Double?,
    val paymentStatus: Int,
    val paymentStatusLabel: String,
    val createdDate: String?,
    val startDate: String?,
    val endDate: String?,
    val oldVal: Int?,
    val newVal: Int?,
    val waterMeterSerial: String?,
    val numOfHouseHold: Int?,
)
