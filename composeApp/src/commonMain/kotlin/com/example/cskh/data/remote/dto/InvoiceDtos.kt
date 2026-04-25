package com.example.cskh.data.remote.dto

import com.example.cskh.domain.model.InvoiceDetail
import com.example.cskh.domain.model.InvoiceSummary
import com.example.cskh.domain.model.PageMeta
import com.example.cskh.domain.model.PagedInvoices
import kotlinx.serialization.Serializable

@Serializable
data class InvoicesPageDataDto(
    val meta: MetaDto,
    val result: List<InvoiceListItemDto>,
)

@Serializable
data class MetaDto(
    val page: Int,
    val pageSize: Int,
    val pages: Int,
    val total: Int,
)

@Serializable
data class InvoiceListItemDto(
    val id: Long,
    val digiCode: String,
    val customerName: String,
    val amount: Double,
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

fun MetaDto.toDomain(): PageMeta = PageMeta(page, pageSize, pages, total)

fun InvoiceListItemDto.toDomain(): InvoiceSummary = InvoiceSummary(
    id = id,
    digiCode = digiCode,
    customerName = customerName,
    amount = amount,
    envFee = envFee ?: 0.0,
    taxFee = taxFee ?: 0.0,
    totalAmount = totalAmount ?: (amount + (envFee ?: 0.0) + (taxFee ?: 0.0)),
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
    meta = meta.toDomain(),
    items = result.map { it.toDomain() },
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
