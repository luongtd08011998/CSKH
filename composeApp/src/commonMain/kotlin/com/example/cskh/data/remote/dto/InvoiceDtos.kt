package com.example.cskh.data.remote.dto

import com.example.cskh.domain.model.EInvoiceData
import com.example.cskh.domain.model.InvoiceDetail
import com.example.cskh.domain.model.InvoiceSummary
import com.example.cskh.domain.model.PageMeta
import com.example.cskh.domain.model.PagedInvoices
import kotlinx.serialization.Serializable

@Serializable
data class BaseErrorResponse(
    val error: String? = null,
    val message: String? = null,
    val statusCode: Int? = null,
)

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
    val fkey: String? = null,
    val blankNo: String? = null,
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
    val fkey: String? = null,
    val blankNo: String? = null,
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
    fkey = fkey.orEmpty(),
    blankNo = blankNo,
)

fun InvoiceDetailDto.toDomain(): InvoiceDetail {
    val resolvedId = monthInvoiceId ?: id ?: 0L
    println("==== DEBUG API PARSING INVOICE: id=$resolvedId, blankNo=$blankNo, fkey=$fkey, rawDto=$this ====")
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
        fkey = fkey,
        blankNo = blankNo,
    )
}

fun InvoicesPageDataDto.toDomain(): PagedInvoices = PagedInvoices(
    meta = meta?.toDomain() ?: PageMeta(1, 20, 1, 0),
    items = (result ?: items)?.map { it.toDomain() }?.filter { it.fkey.isNotBlank() } ?: emptyList(),
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

@Serializable
data class EInvoiceViewDto(
    val invoiceNo: String? = null,
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
    val oldMeterReading: String? = null,
    val newMeterReading: String? = null,
    val waterConsumption: String? = null,
    val waterTaxableAmount: String? = null,
    val vatAmount: String? = null,
    val vatRate: String? = null,
    val envProtectionFee: String? = null,
    val totalAmount: String? = null,
    val totalInWords: String? = null,
    val paymentMethod: String? = null,
    val replacementNote: String? = null,
)

@Serializable
data class EInvoiceViewResponseDto(
    val data: EInvoiceViewDto? = null,
    val message: String? = null,
    val statusCode: Int? = null,
)

fun EInvoiceViewDto.toDomain(): EInvoiceData {
    val taxable = waterTaxableAmount.parseVnMoney() ?: 0.0
    val vat = vatAmount.parseVnMoney() ?: 0.0
    val env = envProtectionFee.parseVnMoney() ?: 0.0
    val calcTotal = taxable + vat + env

    return EInvoiceData(
        invoiceNo = invoiceNo.orEmpty(),
        serialNo = serialNo,
        formCode = formCode,
        invoiceDate = invoiceDate,
        status = status,
        sellerName = sellerName,
        sellerTaxCode = sellerTaxCode,
        sellerAddress = sellerAddress,
        sellerPhone = sellerPhone,
        buyerName = buyerName,
        buyerCode = buyerCode,
        buyerTaxCode = buyerTaxCode,
        buyerAddress = buyerAddress,
        paymentPeriod = paymentPeriod,
        oldMeterReading = oldMeterReading.parseVnMoney()?.toInt(),
        newMeterReading = newMeterReading.parseVnMoney()?.toInt(),
        waterConsumption = waterConsumption.parseVnMoney()?.toInt(),
        waterTaxableAmount = taxable,
        vatAmount = vat,
        vatRate = vatRate?.toDoubleOrNull(),
        envProtectionFee = env,
        totalAmount = totalAmount.parseVnMoney() ?: if (calcTotal > 0) calcTotal else null,
        totalInWords = totalInWords,
        paymentMethod = paymentMethod,
        replacementNote = replacementNote,
    )
}

private fun String?.parseVnMoney(): Double? {
    if (this == null || this.isBlank()) return null
    return this.replace(".", "").replace(",", ".").toDoubleOrNull()
}
