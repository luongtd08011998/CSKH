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
    val yearMonth: String = "",
    val amount: Double,
    val envFee: Double,
    val taxFee: Double,
    val totalAmount: Double,
    val paymentStatus: Int,
    val paymentStatusLabel: String,
    val oldVal: Int,
    val newVal: Int,
    val fkey: String = "",
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
    val fkey: String? = null,
)

enum class InvoiceDisplayType { Normal, Replacement, Replaced }

data class ProcessedInvoice(
    val invoice: InvoiceSummary,
    val displayType: InvoiceDisplayType,
)

data class EInvoiceData(
    val invoiceNo: String,
    val serialNo: String? = null,
    val formCode: String? = null,
    val invoiceDate: String? = null,
    val status: String? = null,
    val sellerName: String? = null,
    val sellerTaxCode: String? = null,
    val sellerAddress: String? = null,
    val sellerPhone: String? = null,
    val buyerName: String? = null,
    val buyerCode: String? = null,
    val buyerTaxCode: String? = null,
    val buyerAddress: String? = null,
    val paymentPeriod: String? = null,
    val oldMeterReading: Int? = null,
    val newMeterReading: Int? = null,
    val waterConsumption: Int? = null,
    val waterTaxableAmount: Double? = null,
    val vatAmount: Double? = null,
    val vatRate: Double? = null,
    val envProtectionFee: Double? = null,
    val totalAmount: Double? = null,
    val totalInWords: String? = null,
    val paymentMethod: String? = null,
    val replacementNote: String? = null,
)
