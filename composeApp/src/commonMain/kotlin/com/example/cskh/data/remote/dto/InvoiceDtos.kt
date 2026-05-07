package com.example.cskh.data.remote.dto

import com.example.cskh.domain.model.InvoiceDetail
import com.example.cskh.domain.model.InvoiceSummary
import com.example.cskh.domain.model.PageMeta
import com.example.cskh.domain.model.PagedInvoices
import kotlinx.serialization.Serializable

@Serializable
data class InvoicesPageDataDto(
    val meta: MetaDto? = null,
    val result: List<InvoiceListItemDto>? = null,
    val items: List<InvoiceListItemDto>? = null,
)

@Serializable
data class MetaDto(
    val page: Int? = null,
    val pageSize: Int? = null,
    val pages: Int? = null,
    val total: Int? = null,
)

@Serializable
data class InvoiceListItemDto(
    val id: Long? = null,
    val digiCode: String? = null,
    val customerName: String? = null,
    val yearMonth: String? = null,
    val amount: Double? = null,
    val envFee: Double? = null,
    val taxFee: Double? = null,
    val totalAmount: Double? = null,
    val paymentStatus: Int? = null,
    val paymentStatusLabel: String? = null,
    val oldVal: Int? = null,
    val newVal: Int? = null,
)

@Serializable
data class InvoiceDetailDto(
    val monthInvoiceId: Long? = null,
    val id: Long? = null,
    val customerId: Long? = null,
    val digiCode: String? = null,
    val customerName: String? = null,
    val yearMonth: String? = null,
    val amount: Double? = null,
    val envFee: Double? = null,
    val taxFee: Double? = null,
    val totalAmount: Double? = null,
    val invStatus: Int? = null,
    val paymentStatus: Int? = null,
    val paymentStatusLabel: String? = null,
    val createdDate: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val oldVal: Int? = null,
    val newVal: Int? = null,
    val waterMeterSerial: String? = null,
    val numOfHouseHold: Int? = null,
)

fun MetaDto.toDomain(): PageMeta = PageMeta(
    page = page ?: 1,
    pageSize = pageSize ?: 20,
    pages = pages ?: 1,
    total = total ?: 0
)

fun InvoiceListItemDto.toDomain(): InvoiceSummary = InvoiceSummary(
    id = id ?: 0L,
    digiCode = digiCode.orEmpty(),
    customerName = customerName.orEmpty(),
    yearMonth = yearMonth.orEmpty(),
    amount = amount ?: 0.0,
    envFee = envFee ?: 0.0,
    taxFee = taxFee ?: 0.0,
    totalAmount = totalAmount ?: ((amount ?: 0.0) + (envFee ?: 0.0) + (taxFee ?: 0.0)),
    paymentStatus = paymentStatus ?: 0,
    paymentStatusLabel = paymentStatusLabel.orEmpty(),
    oldVal = oldVal ?: 0,
    newVal = newVal ?: 0,
)

fun InvoiceDetailDto.toDomain(): InvoiceDetail {
    val resolvedId = monthInvoiceId ?: id ?: 0L
    val pay = paymentStatus ?: invStatus ?: 0
    val label = paymentStatusLabel.orEmpty()
    val amt = amount ?: 0.0
    val env = envFee ?: 0.0
    val tax = taxFee ?: 0.0
    return InvoiceDetail(
        id = resolvedId,
        digiCode = digiCode,
        customerName = customerName,
        customerId = customerId,
        yearMonth = yearMonth,
        amount = amt,
        envFee = env,
        taxFee = tax,
        totalAmount = totalAmount ?: (amt + env + tax),
        paymentStatus = pay,
        paymentStatusLabel = label,
        createdDate = createdDate,
        startDate = startDate,
        endDate = endDate,
        oldVal = oldVal,
        newVal = newVal,
        waterMeterSerial = waterMeterSerial,
        numOfHouseHold = numOfHouseHold,
    )
}

fun InvoicesPageDataDto.toDomain(): PagedInvoices = PagedInvoices(
    meta = meta?.toDomain() ?: PageMeta(1, 20, 1, 0),
    items = (result ?: items)?.map { it.toDomain() } ?: emptyList(),
)

@Serializable
data class InvoicesListResponseDto(
    val data: InvoicesPageDataDto? = null,
    val message: String? = null,
    val statusCode: Int? = null,
)

@Serializable
data class InvoiceDetailResponseDto(
    val data: InvoiceDetailDto? = null,
    val message: String? = null,
    val statusCode: Int? = null,
)
