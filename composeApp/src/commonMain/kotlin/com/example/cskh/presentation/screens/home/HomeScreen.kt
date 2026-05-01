package com.example.cskh.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.foundation.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.cskh.domain.model.CustomerProfile
import com.example.cskh.domain.model.InvoiceDetail
import com.example.cskh.domain.model.InvoiceSummary
import com.example.cskh.presentation.CompanyBranding
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.platform.UriHandler
import com.example.cskh.util.formatVnd
import org.jetbrains.compose.resources.painterResource
import cskh.composeapp.generated.resources.Res
import cskh.composeapp.generated.resources.logocty1

private val pageBackground = Color(0xFFF5F7FA)
private val bannerBlue = Color(0xFF1976D2)
private val serviceCardBg = Color(0xFFF0F2F5)
private val subtitleGray = Color(0xFF757575)
private val cardShadow = 3.dp
private val heroGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF1565C0),
        Color(0xFF0D47A1),
        Color(0xFF0891B2),
    ),
)

private data class HomeServiceCard(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconBackground: Color,
    val iconTint: Color,
    val onClick: () -> Unit,
)

@Composable
fun HomeScreen(
    onNavigateInvoices: () -> Unit,
    onNavigateInvoiceDetail: (Long) -> Unit,
    onNavigateNotifications: () -> Unit,
    onNavigateCustomerProfile: () -> Unit,
    onNavigateWaterPrice: () -> Unit,
    onNavigateAbout: () -> Unit,
    onNavigatePhanAnh: () -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    // Khi refresh token hết hạn → hiển thị thông báo
    if (state.sessionExpired) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { },
            title = { Text("Phiên đăng nhập hết hạn") },
            text = { Text("Phiên làm việc của bạn đã hết hạn. Vui lòng đăng nhập lại để tiếp tục sử dụng.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.acknowledgeSessionExpired()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                ) {
                    Text("OK", color = Color.White)
                }
            }
        )
    }

    val menu = listOf(
        HomeServiceCard(
            title = "Hóa đơn",
            subtitle = "Tra cứu hóa đơn",
            icon = Icons.AutoMirrored.Filled.ReceiptLong,
            iconBackground = Color(0xFFDBEAFE), // blue-100
            iconTint = Color(0xFF2563EB), // blue-600
            onClick = onNavigateInvoices,
        ),
        HomeServiceCard(
            title = "Bảng giá nước",
            subtitle = "Xem bảng giá",
            icon = Icons.Filled.AttachMoney,
            iconBackground = Color(0xFFD1FAE5), // green-100
            iconTint = Color(0xFF059669), // green-600
            onClick = onNavigateWaterPrice,
        ),
        HomeServiceCard(
            title = "Phản ánh",
            subtitle = "Gửi phản ánh",
            icon = Icons.Filled.Feedback,
            iconBackground = Color(0xFFFFF3E0), // orange-100
            iconTint = Color(0xFFEF6C00), // orange-600
            onClick = onNavigatePhanAnh,
        ),
        HomeServiceCard(
            title = "Giới thiệu",
            subtitle = "Về chúng tôi",
            icon = Icons.Filled.Info,
            iconBackground = Color(0xFFF3E8FF), // purple-100
            iconTint = Color(0xFF9333EA), // purple-600
            onClick = onNavigateAbout,
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(pageBackground),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            HomeHero(
                companyName = CompanyBranding.NAME,
                customer = state.customer,
                onOpenProfile = onNavigateCustomerProfile,
            )

            Box {
                // "Card hóa đơn" nổi đè lên header như mẫu
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Spacer(modifier = Modifier.height(0.dp))
                }
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 0.dp)
                        .offset(y = (-24).dp),
                ) {
                    val newest = state.recentInvoices.firstOrNull()
                    CurrentInvoiceCard(
                        invoice = newest,
                        detail = state.currentInvoiceDetail,
                        onThanhToanNgay = {
                            newest?.id?.let { onNavigateInvoiceDetail(it) } ?: onNavigateInvoices()
                        },
                        onXemChiTiet = {
                            newest?.id?.let { onNavigateInvoiceDetail(it) } ?: onNavigateInvoices()
                        },
                        onTraCuuHoaDon = onNavigateInvoices,
                    )
                }
            }

            Spacer(modifier = Modifier.height(0.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "Menu chức năng",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF212121),
                )
                Spacer(modifier = Modifier.height(12.dp))
                MenuGrid(
                    items = menu,
                )

                Spacer(modifier = Modifier.height(18.dp))
                RecentInvoicesSection(
                    invoices = state.recentInvoices.take(3),
                    onViewAll = onNavigateInvoices,
                )

                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun HomeHero(
    companyName: String,
    customer: CustomerProfile?,
    onOpenProfile: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
        color = Color.Transparent,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(heroGradient)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 18.dp),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Surface(
                            modifier = Modifier.size(54.dp),
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 6.dp,
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.logocty1),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(2.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                            )
                        }
                        Column {
                            Text(
                                text = "Công ty TNHH",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Color.White.copy(alpha = 0.9f),
                                ),
                            )
                            Text(
                                text = companyName.replace("Công ty TNHH ", ""),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }

                }

                Spacer(Modifier.height(14.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenProfile),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White.copy(alpha = 0.12f),
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Khách hàng",
                            style = MaterialTheme.typography.labelLarge.copy(color = Color.White.copy(alpha = 0.9f)),
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = customer?.name ?: "—",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            Text(
                                text = "Mã KH: ${customer?.digiCode ?: "—"}",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.9f)),
                            )
                            Text(
                                text = "Địa chỉ: ${customer?.address ?: "—"}",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.9f)),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun isHomeInvoicePaid(invoice: InvoiceSummary?, detail: InvoiceDetail?): Boolean {
    if (invoice == null) return false
    val d = detail?.takeIf { it.id == invoice.id }
    val label = d?.paymentStatusLabel?.takeIf { it.isNotBlank() } ?: invoice.paymentStatusLabel
    if (label.contains("đã thanh toán", ignoreCase = true)) return true
    if (label.contains("chưa thanh toán", ignoreCase = true)) return false
    return when (d?.paymentStatus ?: invoice.paymentStatus) {
        0 -> true
        1 -> false
        else -> false
    }
}

@Composable
private fun CurrentInvoiceCard(
    invoice: InvoiceSummary?,
    detail: InvoiceDetail?,
    onThanhToanNgay: () -> Unit,
    onXemChiTiet: () -> Unit,
    onTraCuuHoaDon: () -> Unit,
) {
    val isPaid = remember(invoice?.id, invoice?.paymentStatusLabel, invoice?.paymentStatus, detail?.id, detail?.paymentStatusLabel, detail?.paymentStatus) {
        isHomeInvoicePaid(invoice, detail)
    }
    val statusText = invoice?.paymentStatusLabel?.takeIf { it.isNotBlank() }
        ?: if (invoice == null) "" else if (isPaid) "Đã thanh toán" else "Chưa thanh toán"
    val statusBg = if (isPaid) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
    val statusColor = if (isPaid) Color(0xFF2E7D32) else Color(0xFFE65100)

    val monthText = remember(detail?.yearMonth) { detail?.yearMonth.toMonthYearDisplay() }
    val dueText = remember(detail?.endDate) { detail?.endDate.toDateDisplay() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = cardShadow),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxSize()
                    .background(Color(0xFF1565C0)),
            )
            Column(modifier = Modifier.padding(16.dp).weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = if (monthText.isNotBlank()) "Hóa đơn tháng $monthText" else "Hóa đơn tháng hiện tại",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF607D8B),
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = invoice?.totalAmount?.formatVnd() ?: "—",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2933),
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    if (invoice != null) {
                        Surface(
                            color = statusBg,
                            shape = RoundedCornerShape(999.dp),
                        ) {
                            Text(
                                text = statusText,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = statusColor,
                            )
                        }
                    }
                }

                if (invoice != null) {
                    Spacer(Modifier.height(10.dp))
                    if (dueText.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Schedule,
                                contentDescription = null,
                                tint = Color(0xFFE65100),
                                modifier = Modifier.size(18.dp),
                            )
                            Text(
                                text = "Hạn thanh toán: $dueText",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = Color(0xFFE65100),
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFFEEF2F6))
                        Spacer(Modifier.height(10.dp))
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Lượng nước tiêu thụ",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF607D8B),
                            )
                            Text(
                                text = "${kotlin.math.max(invoice.newVal - invoice.oldVal, 0)} m³",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = Color(0xFF1F2933),
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))
                val primaryClick = when {
                    invoice == null -> onTraCuuHoaDon
                    isPaid -> onXemChiTiet
                    else -> onThanhToanNgay
                }
                val primaryLabel = when {
                    invoice == null -> "Tra cứu hóa đơn"
                    isPaid -> "Xem chi tiết"
                    else -> "Thanh toán ngay"
                }
                Button(
                    onClick = primaryClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(vertical = 14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                ) {
                    Text(primaryLabel, style = MaterialTheme.typography.titleSmall, color = Color.White)
                }
            }
        }
    }
}

private fun String?.toMonthYearDisplay(): String {
    val s = this?.trim().orEmpty()
    if (s.isBlank()) return ""
    // Accept "YYYY-MM" or "YYYY-MM-..." or "YYYYMM"
    val t = s.take(7)
    return when {
        Regex("\\d{4}-\\d{2}").matches(t) -> "${t.substring(5, 7)}/${t.substring(0, 4)}"
        Regex("\\d{6}").matches(s.take(6)) -> "${s.substring(4, 6)}/${s.substring(0, 4)}"
        else -> s
    }
}

private fun String?.toDateDisplay(): String {
    val s = this?.trim().orEmpty()
    if (s.isBlank()) return ""
    // Accept "YYYY-MM-DD" or "YYYY-MM-DDTHH:mm:ss"
    val d = s.take(10)
    return if (Regex("\\d{4}-\\d{2}-\\d{2}").matches(d)) {
        "${d.substring(8, 10)}/${d.substring(5, 7)}/${d.substring(0, 4)}"
    } else s
}

@Composable
private fun MenuGrid(items: List<HomeServiceCard>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val rows = items.chunked(2)
            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    row.forEach { item ->
                        MenuIconCard(
                            modifier = Modifier.weight(1f),
                            item = item,
                        )
                    }
                    repeat(2 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
private fun MenuIconCard(
    modifier: Modifier,
    item: HomeServiceCard,
) {
    Column(
        modifier = modifier
            .wrapContentHeight()
            .clickable(onClick = item.onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(24.dp),
            color = item.iconBackground,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = item.iconTint,
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
            color = Color(0xFF374151),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun RecentInvoicesSection(
    invoices: List<InvoiceSummary>,
    onViewAll: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Hóa đơn gần đây",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF212121),
        )
        TextButton(onClick = onViewAll) {
            Text("Xem tất cả", color = bannerBlue, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.width(4.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = bannerBlue, modifier = Modifier.size(16.dp))
        }
    }

    Spacer(Modifier.height(10.dp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = cardShadow),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            if (invoices.isEmpty()) {
                Text(
                    text = "Chưa có dữ liệu hóa đơn",
                    style = MaterialTheme.typography.bodyMedium,
                    color = subtitleGray,
                    modifier = Modifier.padding(8.dp),
                )
            } else {
                invoices.forEachIndexed { idx, inv ->
                    RecentInvoiceRow(inv)
                    if (idx != invoices.lastIndex) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFEEF2F6))
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentInvoiceRow(inv: InvoiceSummary) {
    val isPaid = inv.paymentStatusLabel.contains("đã thanh toán", ignoreCase = true)
    val statusBg = if (isPaid) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
    val statusColor = if (isPaid) Color(0xFF2E7D32) else Color(0xFFE65100)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "HĐ ${inv.id}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color(0xFF1F2933),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Sử dụng: ${kotlin.math.max(inv.newVal - inv.oldVal, 0)} m³",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF607D8B),
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = inv.totalAmount.formatVnd(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isPaid) Color(0xFF2E7D32) else Color(0xFFD84315),
            )
            Spacer(Modifier.height(4.dp))
            Surface(color = statusBg, shape = RoundedCornerShape(999.dp)) {
                Text(
                    text = if (isPaid) "Đã thanh toán" else "Chưa TT",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = statusColor,
                )
            }
        }
    }
}

@Composable
private fun CompanyInfoCard(
    uriHandler: UriHandler,
    mapsUrl: String,
    telUri: String,
    mailUri: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Surface(
                    modifier = Modifier.size(60.dp),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 6.dp,
                ) {
                    Image(
                        painter = painterResource(Res.drawable.logocty1),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    ContactLine(
                        icon = Icons.Filled.LocationOn,
                        iconTint = Color(0xFFE53935),
                        text = CompanyBranding.ADDRESS,
                        onClick = { uriHandler.openUri(mapsUrl) },
                    )
                    ContactLine(
                        icon = Icons.Filled.Phone,
                        iconTint = Color(0xFF43A047),
                        text = CompanyBranding.PHONE_DISPLAY,
                        onClick = { uriHandler.openUri(telUri) },
                    )
                    ContactLine(
                        icon = Icons.Filled.Email,
                        iconTint = Color(0xFF1E88E5),
                        text = CompanyBranding.EMAIL,
                        onClick = { uriHandler.openUri(mailUri) },
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 14.dp),
                color = Color(0xFFEEEEEE),
            )


        }
    }
}

@Composable
private fun ContactLine(
    icon: ImageVector,
    iconTint: Color,
    text: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = iconTint,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF424242),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun StatPill(
    icon: ImageVector,
    line1: String,
    line2: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 2.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = bannerBlue,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = line1,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = Color(0xFF212121),
            textAlign = TextAlign.Center,
        )
        Text(
            text = line2,
            style = MaterialTheme.typography.labelSmall,
            color = subtitleGray,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ServiceGridCard(
    modifier: Modifier,
    item: HomeServiceCard,
) {
    Card(
        modifier = modifier
            .heightIn(min = 132.dp)
            .clickable(onClick = item.onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = serviceCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = item.iconBackground,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = Color.White,
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF212121),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = subtitleGray,
                textAlign = TextAlign.Center,
            )
        }
    }
}
