package com.example.cskh.presentation.screens.invoices

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.cskh.domain.model.InvoiceDetail
import com.example.cskh.domain.model.EInvoiceData
import com.example.cskh.presentation.components.SkeletonBox
import com.example.cskh.presentation.components.SkeletonCircle
import com.example.cskh.platform.QrPngSaver
import com.example.cskh.util.formatVnd
import kotlin.math.roundToLong
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import androidx.compose.ui.platform.LocalClipboardManager

private val pageBgTop = Color(0xFFFAFAFA)
private val pageBgBottom = Color(0xFFF0F0F0)
private val headerBlueStart = Color(0xFF42A5F5)
private val headerBlueEnd = Color(0xFF1E88E5)
private val paidGreenBg = Color(0xFFE8F5E9)
private val paidGreenBorder = Color(0xFFA5D6A7)
private val warnOrangeBg = Color(0xFFFFF3E0)
private val warnOrangeBorder = Color(0xFFFFCC80)

private const val VietQrBankId = "970415"
private const val VietQrAccountNo = "113601145666"
private const val VietQrTemplate = "J01ctIp"
private const val VietQrAccountName = "CONG TY TNHH CAP NUOC TOC TIEN"

private enum class InvoicePaymentKind { Paid, Unpaid, Other }

private val HexUpper = "0123456789ABCDEF"

/** RFC 3986 percent-encoding (UTF-8) for query components. */
private fun String.percentEncode(): String {
    val bytes = encodeToByteArray()
    return buildString(bytes.size * 3) {
        for (element in bytes) {
            val b = element.toUByte().toInt()
            when {
                b in 0x41..0x5A || b in 0x61..0x7A || b in 0x30..0x39 -> append(b.toChar())
                b == 0x2D || b == 0x5F || b == 0x2E || b == 0x7E -> append(b.toChar())
                else -> append('%').append(HexUpper[b shr 4]).append(HexUpper[b and 0x0F])
            }
        }
    }
}

private fun buildVietQrQuickLinkUrl(detail: InvoiceDetail): String {
    val code = detail.digiCode?.trim().orEmpty()
    val ym = detail.yearMonth?.trim().orEmpty()
    val addInfo = buildString {
        append("TOCTIEN ")
        if (code.isNotEmpty()) append("$code ")
        if (ym.isNotEmpty()) append(ym)

    }
    val total = detail.totalAmount ?: (detail.amount + detail.envFee + detail.taxFee)
    val amount = total.roundToLong()
    val base = "https://img.vietqr.io/image/$VietQrBankId-$VietQrAccountNo-$VietQrTemplate.png"
    return buildString {
        append(base)
        append("?amount=")
        append(amount)
        append("&addInfo=")
        append(addInfo.percentEncode())
        append("&accountName=")
        append(VietQrAccountName.percentEncode())
    }
}

private fun InvoiceDetail.paymentKind(): InvoicePaymentKind {
    val label = paymentStatusLabel
    if (label.contains("đã thanh toán", ignoreCase = true)) return InvoicePaymentKind.Paid
    if (label.contains("chưa thanh toán", ignoreCase = true)) return InvoicePaymentKind.Unpaid
    return when (paymentStatus) {
        0 -> InvoicePaymentKind.Paid
        1 -> InvoicePaymentKind.Unpaid
        else -> InvoicePaymentKind.Other
    }
}

private fun InvoiceDetail.waterUsedM3(): Int {
    val o = oldVal ?: 0
    val n = newVal ?: 0
    return (n - o).coerceAtLeast(0)
}

private val DAYS_IN_MONTH = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

private fun isLeapYear(y: Int) = (y % 4 == 0 && y % 100 != 0) || (y % 400 == 0)

private fun daysIn(y: Int, m: Int) = if (m == 2 && isLeapYear(y)) 29 else DAYS_IN_MONTH[m - 1]

private fun String?.toDateDisplay(): String {
    val s = this?.trim().orEmpty()
    if (s.isBlank()) return "—"
    val d = s.take(10)
    return if (Regex("\\d{4}-\\d{2}-\\d{2}").matches(d)) {
        "${d.substring(8, 10)}/${d.substring(5, 7)}/${d.substring(0, 4)}"
    } else if (Regex("\\d{8}").matches(s.take(8))) {
        "${s.substring(6, 8)}/${s.substring(4, 6)}/${s.substring(0, 4)}"
    } else s
}

private fun String?.addDays(n: Int): String? {
    val s = this?.trim().orEmpty()
    if (s.isBlank()) return null
    val d = s.take(10)
    var year: Int
    var month: Int
    var day: Int
    if (Regex("\\d{4}-\\d{2}-\\d{2}").matches(d)) {
        year = d.substring(0, 4).toInt()
        month = d.substring(5, 7).toInt()
        day = d.substring(8, 10).toInt() + n
    } else if (Regex("\\d{8}").matches(s.take(8))) {
        year = s.substring(0, 4).toInt()
        month = s.substring(4, 6).toInt()
        day = s.substring(6, 8).toInt() + n
    } else return null
    while (day > daysIn(year, month)) {
        day -= daysIn(year, month)
        month++
        if (month > 12) { month = 1; year++ }
    }
    val dStr = day.toString().padStart(2, '0')
    val mStr = month.toString().padStart(2, '0')
    val yStr = year.toString().padStart(4, '0')
    return "$dStr/$mStr/$yStr"
}

private fun InvoiceDetail.dueDate(): String {
    createdDate.addDays(7)?.let { return it }
    return endDate?.takeIf { it.isNotBlank() }?.toDateDisplay()
        ?: startDate?.takeIf { it.isNotBlank() }?.toDateDisplay()
        ?: "—"
}

@Composable
fun InvoiceDetailScreen(
    invoiceId: Long,
    onBack: () -> Unit,
    onLogout: () -> Unit,
) {
    val viewModel: InvoiceDetailViewModel = koinViewModel(parameters = { parametersOf(invoiceId) })
    val state by viewModel.state.collectAsState()

    if (state.sessionExpired) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Phiên đăng nhập hết hạn") },
            text = { Text("Phiên làm việc của bạn đã hết hạn. Vui lòng đăng nhập lại để tiếp tục sử dụng.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.acknowledgeSessionExpired()
                        onLogout()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
    val clipboard = LocalClipboardManager.current
    var showVietQrDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(colors = listOf(pageBgTop, pageBgBottom)),
            ),
    ) {
        when {
            state.isLoading && state.detail == null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            state.detail != null -> {
                val d = state.detail!!
                val scroll = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scroll),
                ) {
                    DetailHeader(
                        detail = d,
                        onBack = onBack,
                        onShare = {
                            val text = buildString {
                                appendLine("Hóa đơn #${d.id}")
                                d.yearMonth?.let { appendLine("Kỳ: $it") }
                                appendLine(d.paymentStatusLabel)
                                d.totalAmount?.let { appendLine("Tổng: ${it.formatVnd()}") }
                            }
                            clipboard.setText(AnnotatedString(text))
                        },
                        onDownloadEInvoiceZip = { viewModel.onDownloadEInvoiceZip() },
                        isEInvoiceDownloading = state.isEInvoiceDownloading,
                        eInvoiceMessage = state.eInvoiceMessage,
                        showInfoCard = state.eInvoiceData == null && !state.isEInvoiceViewLoading
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-12).dp)
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 24.dp),
                    ) {
                        val ei = state.eInvoiceData
                        val isLoadingEi = state.isEInvoiceViewLoading
                        val isReplacement = d.totalAmount == 0.0

                        if (isReplacement) {
                            ReplacementNoteCard()
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (isLoadingEi) {
                            EInvoiceSkeleton()
                        } else if (ei != null) {
                            EInvoiceHeader(ei, d.paymentStatusLabel)
                            Spacer(modifier = Modifier.height(12.dp))


                            EInvoiceMainCard(ei)
                            Spacer(modifier = Modifier.height(16.dp))

                            if (!d.isPaid()) {
                                UnpaidWarningCard(d)
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            EInvoiceFooterSection(
                                eInvoiceData = ei,
                                isReplacement = isReplacement,
                                isEInvoiceDownloading = state.isEInvoiceDownloading,
                                isPaid = d.paymentStatusLabel.contains("đã thanh toán", ignoreCase = true),
                                hasAmount = (d.totalAmount ?: 0.0) > 0,
                                onDownload = { viewModel.onDownloadEInvoiceZip() },
                                onPayInvoice = { showVietQrDialog = true },
                            )
                        } else {
                            CustomerInfoCard(d)
                            Spacer(modifier = Modifier.height(12.dp))
                            WaterUsageCard(d)
                            Spacer(modifier = Modifier.height(12.dp))
                            PaymentBreakdownCard(d)
                            Spacer(modifier = Modifier.height(12.dp))
                            PaymentDatesCard(d)
                            state.eInvoiceViewError?.let { err ->
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(err, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                                OutlinedButton(onClick = { viewModel.loadEInvoiceView() }) { Text("Thử lại tải HĐ điện tử") }
                            }
                        }

                        state.errorMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = msg,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = state.errorMessage ?: "Không có dữ liệu",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.load() }) {
                        Text("Thử lại")
                    }
                }
            }
        }
        if (showVietQrDialog && state.detail != null) {
            val d = state.detail!!
            VietQrPaymentDialog(
                imageUrl = buildVietQrQuickLinkUrl(d),
                onDismiss = { showVietQrDialog = false },
            )
        }
    }
}

@Composable
private fun VietQrPaymentDialog(
    imageUrl: String,
    onDismiss: () -> Unit,
) {
    val qrPngSaver = koinInject<QrPngSaver>()
    val scope = rememberCoroutineScope()
    var isSavingQr by remember { mutableStateOf(false) }
    var saveFeedback by remember { mutableStateOf<String?>(null) }
    var saveFeedbackIsError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    onClick = {
                        if (isSavingQr) return@TextButton
                        scope.launch {
                            isSavingQr = true
                            saveFeedback = null
                            qrPngSaver.savePngFromUrl(imageUrl).fold(
                                onSuccess = {
                                    saveFeedback = it
                                    saveFeedbackIsError = false
                                },
                                onFailure = { e ->
                                    saveFeedback = e.message ?: "Không lưu được ảnh"
                                    saveFeedbackIsError = true
                                },
                            )
                            isSavingQr = false
                        }
                    },
                    enabled = !isSavingQr,
                ) {
                    if (isSavingQr) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("Lưu mã QR")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDismiss, enabled = !isSavingQr) {
                    Text("Đóng")
                }
            }
        },
        title = {
            Text(
                "Thanh toán qua VietQR",
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Mã QR VietQR thanh toán",
                    modifier = Modifier.fillMaxWidth(),
                )
                saveFeedback?.let { msg ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = msg,
                        color = if (saveFeedbackIsError) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        },
    )
}

@Composable
private fun DetailHeader(
    detail: InvoiceDetail,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onDownloadEInvoiceZip: () -> Unit,
    isEInvoiceDownloading: Boolean,
    eInvoiceMessage: String?,
    showInfoCard: Boolean = true,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(headerBlueStart, headerBlueEnd),
                ),
            )
            .statusBarsPadding()
            .padding(bottom = 20.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = Color.White,
                )
            }
            Text(
                text = "Chi tiết hóa đơn",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                ),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            IconButton(onClick = onShare) {
                Icon(
                    Icons.Filled.Share,
                    contentDescription = "Chia sẻ",
                    tint = Color.White,
                )
            }
        }
        if (showInfoCard) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.14f),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    HeaderRow(
                        icon = { Icon(Icons.Filled.Description, null, tint = Color.White, modifier = Modifier.size(20.dp)) },
                        label = "Mã hóa đơn",
                        value = "#${detail.id}",
                        valueMono = true,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    HeaderRow(
                        icon = { Icon(Icons.Filled.CalendarMonth, null, tint = Color.White, modifier = Modifier.size(20.dp)) },
                        label = "Kỳ hóa đơn",
                        value = detail.yearMonth ?: "—",
                        valueMono = false,
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 12.dp),
                        color = Color.White.copy(alpha = 0.25f),
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Trạng thái",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f),
                        )
                        StatusPill(detail)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = onDownloadEInvoiceZip,
                        enabled = !isEInvoiceDownloading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.85f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White,
                            disabledContentColor = Color.White.copy(alpha = 0.5f),
                        ),
                    ) {
                        if (isEInvoiceDownloading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Tải hóa đơn điện tử (.zip)")
                    }
                    eInvoiceMessage?.let { msg ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF1B5E20).copy(alpha = 0.35f),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top,
                            ) {
                                Icon(
                                    Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFFA5D6A7),
                                    modifier = Modifier.size(18.dp),
                                )
                                Text(
                                    text = msg,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFC8E6C9),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderRow(
    icon: @Composable () -> Unit,
    label: String,
    value: String,
    valueMono: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            icon()
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f),
            )
        }
        Text(
            text = value,
            style = if (valueMono) {
                MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace)
            } else {
                MaterialTheme.typography.bodyMedium
            },
            color = Color.White,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun StatusPill(detail: InvoiceDetail) {
    val kind = detail.paymentKind()
    val (bg, fg, icon) = when (kind) {
        InvoicePaymentKind.Paid -> Triple(Color(0x332E7D32), Color(0xFFC8E6C9), Icons.Filled.CheckCircle)
        InvoicePaymentKind.Unpaid -> Triple(Color(0x33EF6C00), Color(0xFFFFE0B2), Icons.Filled.Warning)
        InvoicePaymentKind.Other -> Triple(Color(0x33C62828), Color(0xFFFFCDD2), Icons.Filled.Cancel)
    }
    Surface(
        shape = RoundedCornerShape(50),
        color = bg,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = fg)
            Text(
                text = detail.paymentStatusLabel.ifBlank { "—" },
                style = MaterialTheme.typography.labelMedium,
                color = fg,
            )
        }
    }
}

@Composable
private fun CustomerInfoCard(d: InvoiceDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Thông tin khách hàng",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color(0xFF212121),
            )
            Spacer(modifier = Modifier.height(14.dp))
            InfoRow(
                icon = Icons.Filled.Person,
                iconBg = Color(0xFFE3F2FD),
                iconTint = Color(0xFF1976D2),
                label = "Tên khách hàng",
                value = d.customerName ?: "—",
                mono = false,
                showDivider = true,
            )
            InfoRow(
                icon = Icons.Filled.Tag,
                iconBg = Color(0xFFF3E5F5),
                iconTint = Color(0xFF8E24AA),
                label = "Mã khách hàng",
                value = d.digiCode ?: "—",
                mono = true,
                showDivider = false,
            )
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    label: String,
    value: String,
    mono: Boolean,
    showDivider: Boolean,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(shape = RoundedCornerShape(8.dp), color = iconBg) {
                Icon(icon, null, modifier = Modifier.padding(8.dp).size(22.dp), tint = iconTint)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.bodySmall, color = Color(0xFF757575))
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    value,
                    style = if (mono) {
                        MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace)
                    } else {
                        MaterialTheme.typography.bodyLarge
                    },
                    color = Color(0xFF212121),
                )
            }
        }
        if (showDivider) {
            HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}

@Composable
private fun WaterUsageCard(d: InvoiceDetail) {
    val used = d.waterUsedM3()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Lượng nước tiêu thụ",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color(0xFF212121),
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                WaterStatBox(
                    modifier = Modifier.weight(1f),
                    bg = Color(0xFFE3F2FD),
                    icon = Icons.Filled.WaterDrop,
                    iconTint = Color(0xFF1976D2),
                    value = "${d.oldVal ?: "—"}",
                    caption = "Chỉ số cũ (m³)",
                )
                WaterStatBox(
                    modifier = Modifier.weight(1f),
                    bg = Color(0xFFE8F5E9),
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    iconTint = Color(0xFF43A047),
                    value = "${d.newVal ?: "—"}",
                    caption = "Chỉ số mới (m³)",
                )
                WaterStatBox(
                    modifier = Modifier.weight(1f),
                    bg = Color(0xFFFFF3E0),
                    icon = Icons.AutoMirrored.Filled.ReceiptLong,
                    iconTint = Color(0xFFE65100),
                    value = "$used",
                    caption = "Tiêu thụ (m³)",
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Transparent,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFFE3F2FD), Color(0xFFE8F5E9)),
                            ),
                        )
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Surface(shape = CircleShape, color = Color.White, shadowElevation = 2.dp) {
                        Box(
                            modifier = Modifier.size(48.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.WaterDrop, null, tint = Color(0xFF1976D2), modifier = Modifier.size(26.dp))
                        }
                    }
                    Column {
                        Text("Lượng nước sử dụng", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF424242))
                        Text(
                            text = "$used m³",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1565C0),
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WaterStatBox(
    modifier: Modifier,
    bg: Color,
    icon: ImageVector,
    iconTint: Color,
    value: String,
    caption: String,
) {
    Column(
        modifier = modifier
            .background(bg, RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, null, modifier = Modifier.size(26.dp), tint = iconTint)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = Color(0xFF212121),
        )
        Text(
            text = caption,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF757575),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PaymentBreakdownCard(d: InvoiceDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Chi tiết thanh toán",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color(0xFF212121),
            )
            Spacer(modifier = Modifier.height(12.dp))
            MoneyLine(Icons.Filled.WaterDrop, Color(0xFF1976D2), "Tiền nước", d.amount.formatVnd())
            MoneyLine(Icons.Filled.Park, Color(0xFF43A047), "Phí môi trường", d.envFee.formatVnd())
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.AutoMirrored.Filled.ReceiptLong,
                        null,
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFF8E24AA),
                    )
                    Text("Thuế GTGT (5%)", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF616161))
                }
                Text(d.taxFee.formatVnd(), style = MaterialTheme.typography.bodyLarge, color = Color(0xFF212121))
            }
            HorizontalDivider(color = Color(0xFFE0E0E0))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(colors = listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB))),
                        RoundedCornerShape(12.dp),
                    )
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.AttachMoney, null, tint = Color(0xFF1565C0), modifier = Modifier.size(22.dp))
                    Text(
                        "Tổng cộng",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Color(0xFF212121),
                    )
                }
                Text(
                    text = (d.totalAmount ?: (d.amount + d.envFee + d.taxFee)).formatVnd(),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0),
                    ),
                )
            }
        }
    }
}

@Composable
private fun MoneyLine(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(18.dp), tint = iconTint)
            Text(label, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF616161))
        }
        Text(value, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF212121))
    }
}

@Composable
private fun PaymentDatesCard(d: InvoiceDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Thông tin thanh toán",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color(0xFF212121),
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                DateBox(
                    modifier = Modifier.weight(1f),
                    bg = Color(0xFFF5F5F5),
                    label = "Ngày phát hành",
                    value = d.createdDate.toDateDisplay(),
                    valueAccent = Color(0xFF212121),
                )
                DateBox(
                    modifier = Modifier.weight(1f),
                    bg = Color(0xFFFFF3E0),
                    label = "Hạn thanh toán",
                    valueAccent = Color(0xFFE65100),
                    subLabel = "7 ngày kể từ ngày phát hành hóa đơn"
                )
            }
        }
    }
}

@Composable
private fun DateBox(
    modifier: Modifier,
    bg: Color,
    label: String,
    value: String? = null,
    valueAccent: Color,
    subLabel: String? = null,
) {
    Column(
        modifier = modifier
            .background(bg, RoundedCornerShape(12.dp))
            .padding(12.dp),
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color(0xFF757575))
        Spacer(modifier = Modifier.height(4.dp))
        value?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = valueAccent,
            )
        }
        subLabel?.let {
            if (value != null) Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = it,
                style = if (value == null) {
                    MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                } else {
                    MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal)
                },
                color = valueAccent.copy(alpha = 0.85f),
            )
        }
    }
}

@Composable
private fun UnpaidWarningCard(d: InvoiceDetail) {
    val due = d.dueDate()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = warnOrangeBg,
        border = BorderStroke(2.dp, warnOrangeBorder),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(Icons.Filled.Warning, null, tint = Color(0xFFE65100), modifier = Modifier.size(22.dp))
            Column {
                Text(
                    "Lưu ý thanh toán",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = Color(0xFFE65100),
                )
                Spacer(modifier = Modifier.height(4.dp))
                val dueSuffix = if (due != "—") " ($due)" else ""
                Text(
                    text = "Vui lòng thanh toán đúng hạn để tránh bị cắt nước.$dueSuffix",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFBF360C),
                )
            }
        }
    }
}

@Composable
private fun EInvoiceHeader(ei: EInvoiceData, paymentStatusLabel: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFE3F2FD),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Mã hóa đơn: ${ei.invoiceNo.ifBlank { "—" }}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = Color(0xFF1565C0),
            )
            if (!ei.formCode.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Mẫu số: ${ei.formCode}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = Color(0xFF546E7A),
                )
            }
            ei.invoiceDate?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ngày phát hành: ${it.toDateDisplay()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF546E7A),
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            val isPaid = paymentStatusLabel.contains("đã thanh toán", ignoreCase = true)
            val displayStatus = paymentStatusLabel.uppercase()
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isPaid) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        if (isPaid) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (isPaid) Color(0xFF2E7D32) else Color(0xFFC62828),
                    )
                    Text(
                        text = displayStatus,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = if (isPaid) Color(0xFF2E7D32) else Color(0xFFC62828),
                    )
                }
            }
        }
    }
}



@Composable
private fun EInvoicePaymentCard(ei: EInvoiceData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            ei.waterTaxableAmount?.let {
                EInvoiceMoneyRow(Icons.Filled.WaterDrop, Color(0xFF1976D2), "Tiền nước (chưa thuế)", it.formatVnd())
            }
            ei.envProtectionFee?.let {
                EInvoiceMoneyRow(Icons.Filled.Park, Color(0xFF43A047), "Phí bảo vệ môi trường", it.formatVnd())
            }
            ei.vatAmount?.let {
                val rateLabel = ei.vatRate?.let { r -> "Thuế GTGT (${(r * 100).toInt()}%)" } ?: "Thuế GTGT"
                EInvoiceMoneyRow(Icons.AutoMirrored.Filled.ReceiptLong, Color(0xFF8E24AA), rateLabel, it.formatVnd())
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF5F5F5))
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0xFFE3F2FD), RoundedCornerShape(12.dp)).padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("TỔNG CỘNG", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = Color(0xFF1565C0))
                Text(
                    ei.totalAmount?.formatVnd() ?: "—",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, color = Color(0xFF1565C0)),
                )
            }
            ei.totalInWords?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        "Bằng chữ:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF757575)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        it,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Medium,
                            lineHeight = androidx.compose.ui.unit.TextUnit.Unspecified
                        ),
                        color = Color(0xFF424242)
                    )
                }
            }
        }
    }
}

@Composable
private fun EInvoiceMoneyRow(icon: ImageVector, iconTint: Color, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(18.dp), tint = iconTint)
            Text(label, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF616161))
        }
        Text(value, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF212121))
    }
}

@Composable
private fun EInvoiceFooterSection(
    eInvoiceData: EInvoiceData,
    isReplacement: Boolean,
    isEInvoiceDownloading: Boolean,
    isPaid: Boolean,
    hasAmount: Boolean,
    onDownload: () -> Unit,
    onPayInvoice: () -> Unit,
) {
    Column {
        eInvoiceData.replacementNote?.let { note ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFFF3E0),
                border = BorderStroke(1.dp, Color(0xFFFFCC80)),
            ) {
                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Filled.Description, null, tint = Color(0xFFE65100), modifier = Modifier.size(18.dp))
                    Text(note, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = Color(0xFFBF360C))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = onDownload,
                enabled = !isEInvoiceDownloading,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF1976D2)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1976D2)),
            ) {
                if (isEInvoiceDownloading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color(0xFF1976D2))
                } else {
                    Icon(Icons.Filled.Download, null, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.size(6.dp))
                Text("Tải HĐ (.zip)", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }
            if (!isPaid && hasAmount && !isReplacement) {
                Button(
                    onClick = onPayInvoice,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(Icons.Filled.CreditCard, null, modifier = Modifier.size(18.dp), tint = Color.White)
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("Thanh toán ngay", color = Color.White, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

@Composable
private fun EInvoiceSkeleton() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Section 1: Header skeleton
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = Color.White) {
            Column(modifier = Modifier.padding(18.dp)) {
                SkeletonBox(modifier = Modifier.fillMaxWidth(0.3f), height = 14.dp)
                Spacer(modifier = Modifier.height(8.dp))
                SkeletonBox(modifier = Modifier.fillMaxWidth(0.6f), height = 32.dp)
                Spacer(modifier = Modifier.height(12.dp))
                SkeletonBox(modifier = Modifier.fillMaxWidth(0.4f), height = 16.dp)
                Spacer(modifier = Modifier.height(16.dp))
                SkeletonBox(modifier = Modifier.width(140.dp), height = 32.dp)
            }
        }
        // Section 2: Seller skeleton
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = Color.White) {
            Column(modifier = Modifier.padding(18.dp)) {
                SkeletonBox(modifier = Modifier.fillMaxWidth(0.4f), height = 18.dp)
                Spacer(modifier = Modifier.height(16.dp))
                repeat(3) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        SkeletonCircle(size = 18.dp)
                        Column(modifier = Modifier.weight(1f)) {
                            SkeletonBox(modifier = Modifier.fillMaxWidth(0.3f), height = 10.dp)
                            Spacer(modifier = Modifier.height(4.dp))
                            SkeletonBox(modifier = Modifier.fillMaxWidth(0.8f), height = 14.dp)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
        // Section 3: Buyer skeleton
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = Color.White) {
            Column(modifier = Modifier.padding(18.dp)) {
                SkeletonBox(modifier = Modifier.fillMaxWidth(0.5f), height = 18.dp)
                Spacer(modifier = Modifier.height(16.dp))
                repeat(2) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        SkeletonCircle(size = 18.dp)
                        Column(modifier = Modifier.weight(1f)) {
                            SkeletonBox(modifier = Modifier.fillMaxWidth(0.3f), height = 10.dp)
                            Spacer(modifier = Modifier.height(4.dp))
                            SkeletonBox(modifier = Modifier.fillMaxWidth(0.7f), height = 14.dp)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
        // Section 4: Consumption skeleton
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = Color.White) {
            Column(modifier = Modifier.padding(18.dp)) {
                SkeletonBox(modifier = Modifier.fillMaxWidth(0.4f), height = 18.dp)
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(3) { SkeletonBox(modifier = Modifier.weight(1f), height = 60.dp, cornerRadius = 12.dp) }
                }
            }
        }
        // Section 5: Payment skeleton
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = Color.White) {
            Column(modifier = Modifier.padding(18.dp)) {
                SkeletonBox(modifier = Modifier.fillMaxWidth(0.4f), height = 18.dp)
                Spacer(modifier = Modifier.height(16.dp))
                repeat(3) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        SkeletonBox(modifier = Modifier.width(100.dp), height = 14.dp)
                        SkeletonBox(modifier = Modifier.width(80.dp), height = 14.dp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                SkeletonBox(modifier = Modifier.fillMaxWidth(), height = 50.dp, cornerRadius = 12.dp)
            }
        }
    }
}

@Composable
private fun EInvoiceInfoRow(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String,
    isBoldValue: Boolean = false,
    mono: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, modifier = Modifier.size(16.dp), tint = iconTint)
            Text(label, style = MaterialTheme.typography.bodySmall, color = Color(0xFF757575))
        }
        Text(
            value,
            modifier = Modifier.weight(1.2f),
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isBoldValue) FontWeight.Bold else FontWeight.Medium,
                fontFamily = if (mono) FontFamily.Monospace else FontFamily.Default
            ),
            color = Color(0xFF212121),
            textAlign = TextAlign.End
        )
    }
}
@Composable
private fun EInvoiceMainCard(ei: EInvoiceData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            EInvoiceBuyerContent(ei)
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 14.dp),
                thickness = 0.5.dp,
                color = Color.LightGray.copy(alpha = 0.3f)
            )
            EInvoiceConsumptionContent(ei)
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 14.dp),
                thickness = 0.5.dp,
                color = Color.LightGray.copy(alpha = 0.3f)
            )
            EInvoicePaymentContent(ei)
        }
    }
}

@Composable
private fun EInvoiceBuyerContent(ei: EInvoiceData) {
    ei.buyerName?.let {
        EInvoiceInfoRow(Icons.Filled.Person, Color(0xFF1976D2), "Tên khách hàng", it, isBoldValue = true)
    }
    ei.buyerCode?.let {
        EInvoiceInfoRow(Icons.Filled.Tag, Color(0xFF8E24AA), "Mã khách hàng", it, mono = true)
    }
    ei.buyerTaxCode?.let {
        EInvoiceInfoRow(Icons.Filled.Tag, Color(0xFF8E24AA), "Mã số thuế", it, mono = true)
    }
    ei.buyerAddress?.let {
        EInvoiceInfoRow(Icons.Filled.CalendarMonth, Color(0xFF546E7A), "Địa chỉ", it)
    }
}

@Composable
private fun EInvoiceConsumptionContent(ei: EInvoiceData) {
    ei.paymentPeriod?.let {
        EInvoiceInfoRow(Icons.Filled.CalendarMonth, Color(0xFF1976D2), "Kỳ thanh toán", it)
    }
    Spacer(modifier = Modifier.height(10.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f).background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp)).padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Chỉ số cũ", style = MaterialTheme.typography.labelSmall, color = Color(0xFF757575))
            Text(ei.oldMeterReading?.toString() ?: "—", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF424242))
        }
        Column(
            modifier = Modifier.weight(1f).background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp)).padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Chỉ số mới", style = MaterialTheme.typography.labelSmall, color = Color(0xFF757575))
            Text(ei.newMeterReading?.toString() ?: "—", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF424242))
        }
        Column(
            modifier = Modifier.weight(1.2f).background(Color(0xFFE3F2FD), RoundedCornerShape(12.dp)).padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Tiêu thụ (m³)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF1565C0))
            val consumed = ei.waterConsumption ?: run {
                val old = ei.oldMeterReading ?: 0
                val new = ei.newMeterReading ?: 0
                (new - old).coerceAtLeast(0)
            }
            Text("$consumed", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold), color = Color(0xFF1565C0))
        }
    }
}

@Composable
private fun EInvoicePaymentContent(ei: EInvoiceData) {
    ei.waterTaxableAmount?.let {
        EInvoiceMoneyRow(Icons.Filled.WaterDrop, Color(0xFF1976D2), "Tiền nước (chưa thuế)", it.formatVnd())
    }
    ei.envProtectionFee?.let {
        EInvoiceMoneyRow(Icons.Filled.Park, Color(0xFF43A047), "Phí bảo vệ môi trường", it.formatVnd())
    }
    ei.vatAmount?.let {
        val rateLabel = if (ei.vatRate != null) "Thuế GTGT (${ei.vatRate})" else "Thuế GTGT"
        EInvoiceMoneyRow(Icons.AutoMirrored.Filled.ReceiptLong, Color(0xFF8E24AA), rateLabel, it.formatVnd())
    }
    HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), thickness = 1.dp, color = Color(0xFFEEEEEE))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("TỔNG CỘNG", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF212121))
        Text(
            ei.totalAmount?.formatVnd() ?: "—",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
            color = Color(0xFF1565C0),
        )
    }
}


private fun InvoiceDetail.isPaid(): Boolean =
    paymentStatusLabel.contains("đã thanh toán", ignoreCase = true)

@Composable
private fun ReplacementNoteCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFE3F2FD),
        border = BorderStroke(1.dp, Color(0xFF90CAF9)),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Description,
                null,
                tint = Color(0xFF1565C0),
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    "Hóa đơn thay thế/Hủy",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF1565C0)
                )
                Text(
                    "Đây là hóa đơn điều chỉnh dữ liệu cho kỳ này, hóa đơn trước đó đã được hủy bỏ.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF42A5F5)
                )
            }
        }
    }
}
