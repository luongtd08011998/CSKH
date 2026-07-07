package com.example.cskh.presentation.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Receipt
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
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.ui.unit.sp
import com.example.cskh.domain.model.CustomerProfile
import com.example.cskh.domain.model.InvoiceDetail
import com.example.cskh.domain.model.InvoiceSummary
import com.example.cskh.presentation.CompanyBranding
import com.example.cskh.presentation.NotificationBadgeStore
import com.example.cskh.presentation.components.DataLoadingStatusBar
import com.example.cskh.presentation.components.HomeSkeletonContent
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.platform.UriHandler
import com.example.cskh.util.formatVnd
import org.jetbrains.compose.resources.painterResource
import cskh.composeapp.generated.resources.Res
import cskh.composeapp.generated.resources.logocty1
import cskh.composeapp.generated.resources.banner_1
import cskh.composeapp.generated.resources.banner_2
import cskh.composeapp.generated.resources.banner_3
import cskh.composeapp.generated.resources.banner_4
import cskh.composeapp.generated.resources.banner_5

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
    val badge: Int = 0,
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
    onNavigateFeedbackNotifications: () -> Unit = {},
    onLogout: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val notificationBadgeStore = koinInject<NotificationBadgeStore>()
    val feedbackUnreadCount by notificationBadgeStore.feedbackUnreadCount.collectAsState()

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
            badge = feedbackUnreadCount,
            onClick = onNavigateFeedbackNotifications,
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

    val showSkeleton = state.isLoading && state.customer == null

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
            // ── Giai đoạn 2: Skeleton loading khi chưa có data ──
            AnimatedContent(
                targetState = showSkeleton,
                transitionSpec = {
                    fadeIn(tween(350)) togetherWith fadeOut(tween(200))
                },
                label = "homeContentTransition",
                modifier = Modifier.fillMaxWidth(),
            ) { isSkeleton ->
                if (isSkeleton) {
                    HomeSkeletonContent()
                } else {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        HomeHero(
                            companyName = CompanyBranding.NAME,
                            customer = state.customer,
                            onOpenProfile = onNavigateCustomerProfile,
                        )


                        Box(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 8.dp)
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

                        CompanyBannerCarousel()
                        Spacer(modifier = Modifier.height(16.dp))

                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Spacer(modifier = Modifier.height(8.dp))
                            MenuGrid(items = menu)
                            Spacer(modifier = Modifier.height(18.dp))
                        }
                    }
                }
            }

            // ── Status bar: loading/slow-connection (luôn hiển thị phía dưới) ──
            DataLoadingStatusBar(
                isLoading = state.isLoading,
                isSlowConnection = state.isSlowConnection,
                onRetry = { viewModel.retry() },
                modifier = Modifier.padding(bottom = 8.dp),
            )
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
        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
        shadowElevation = 8.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(Color(0xFF0D47A1), Color(0xFF0288D1))))
                .padding(horizontal = 24.dp, vertical = 8.dp),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top Left: Logo + Company Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Surface(
                        modifier = Modifier.size(42.dp),
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 2.dp,
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.logocty1),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                        )
                    }
                    Column {
                        Text(
                            text = "Công ty TNHH",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Medium
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
                Spacer(Modifier.height(10.dp))

                // Customer Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenProfile),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White.copy(alpha = 0.15f),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Xin chào, ",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                )
                                Text(
                                    text = customer?.name ?: "—",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            Text(
                                text = "Mã KH: ${customer?.digiCode ?: "—"}",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White.copy(alpha = 0.9f)
                                ),
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
    val isReplacement = invoice?.totalAmount == 0.0
    val statusText = when {
        isReplacement -> "HĐ Thay thế/Hủy"
        invoice?.paymentStatusLabel?.isNotBlank() == true -> invoice.paymentStatusLabel
        invoice == null -> ""
        isPaid -> "Đã thanh toán"
        else -> "Chưa thanh toán"
    }
    val statusBg = when {
        isReplacement -> Color(0xFFE3F2FD)
        isPaid -> Color(0xFFE8F5E9)
        else -> Color(0xFFFFF3E0)
    }
    val statusColor = when {
        isReplacement -> Color(0xFF1565C0)
        isPaid -> Color(0xFF2E7D32)
        else -> Color(0xFFE65100)
    }

    val monthText = remember(invoice?.yearMonth, detail?.yearMonth) { 
        (detail?.yearMonth ?: invoice?.yearMonth).orEmpty().toMonthYearDisplay() 
    }
    
    val dueText = remember(detail?.endDate, invoice?.yearMonth) {
        val detailEnd = detail?.endDate.toDateDisplay()
        if (detailEnd.isNotBlank()) detailEnd else {
            val ym = invoice?.yearMonth?.trim() ?: ""
            val year = when {
                Regex("\\d{4}-\\d{2}").matches(ym.take(7)) -> ym.substring(0, 4).toIntOrNull() ?: 0
                Regex("\\d{6}").matches(ym.take(6)) -> ym.substring(0, 4).toIntOrNull() ?: 0
                else -> 0
            }
            val month = when {
                Regex("\\d{4}-\\d{2}").matches(ym.take(7)) -> ym.substring(5, 7).toIntOrNull() ?: 0
                Regex("\\d{6}").matches(ym.take(6)) -> ym.substring(4, 6).toIntOrNull() ?: 0
                else -> 0
            }
            if (year > 0 && month > 0) {
                val nextMonth = if (month == 12) 1 else month + 1
                val nextYear = if (month == 12) year + 1 else year
                "07/${nextMonth.toString().padStart(2, '0')}/$nextYear"
            } else ""
        }
    }

    val periodText = remember(detail?.startDate, detail?.endDate, invoice?.yearMonth) {
        val startStr = detail?.startDate.toDateDisplay()
        val endStr = detail?.endDate.toDateDisplay()
        if (startStr.isNotBlank() && endStr.isNotBlank()) {
            "${startStr.take(5)} - ${endStr.take(5)}"
        } else {
            val ym = invoice?.yearMonth?.trim() ?: ""
            val year = when {
                Regex("\\d{4}-\\d{2}").matches(ym.take(7)) -> ym.substring(0, 4).toIntOrNull() ?: 0
                Regex("\\d{6}").matches(ym.take(6)) -> ym.substring(0, 4).toIntOrNull() ?: 0
                else -> 0
            }
            val month = when {
                Regex("\\d{4}-\\d{2}").matches(ym.take(7)) -> ym.substring(5, 7).toIntOrNull() ?: 0
                Regex("\\d{6}").matches(ym.take(6)) -> ym.substring(4, 6).toIntOrNull() ?: 0
                else -> 0
            }
            if (year > 0 && month > 0) {
                val daysInMonth = when (month) {
                    2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
                    4, 6, 9, 11 -> 30
                    else -> 31
                }
                "01/${month.toString().padStart(2, '0')} - $daysInMonth/${month.toString().padStart(2, '0')}"
            } else "—"
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp).fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (monthText.isNotBlank()) "Hóa đơn tháng $monthText" else "Hóa đơn tháng hiện tại",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF607D8B),
                )
                if (invoice != null) {
                    Surface(
                        color = statusBg,
                        shape = RoundedCornerShape(999.dp),
                    ) {
                        Text(
                            text = statusText,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = statusColor,
                        )
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = invoice?.totalAmount?.formatVnd()?.replace(" đ", "₫")?.replace(" VNĐ", "₫") ?: "—",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2), // Primary Blue
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (invoice != null) {
                Spacer(Modifier.height(6.dp))
                
                // Dashed Divider 1
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
                    drawLine(
                        color = Color(0xFFCFD8DC),
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                        strokeWidth = 2f,
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(16f, 16f), 0f)
                    )
                }
                
                Spacer(Modifier.height(6.dp))
                
                // Rows
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Lượng nước tiêu thụ
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = Color(0xFFE3F2FD), modifier = Modifier.size(28.dp)) {
                                Box(contentAlignment = Alignment.Center) { Icon(Icons.Filled.WaterDrop, contentDescription = null, tint = Color(0xFF1976D2), modifier = Modifier.size(14.dp)) }
                            }
                            Spacer(Modifier.width(12.dp))
                            Text("Lượng nước tiêu thụ", style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF607D8B)))
                        }
                        Text("${kotlin.math.max(invoice.newVal - invoice.oldVal, 0)} m³", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1F2933)))
                    }
                    
                    // Hạn thanh toán
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = Color(0xFFFFF3E0), modifier = Modifier.size(28.dp)) {
                                Box(contentAlignment = Alignment.Center) { Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(14.dp)) }
                            }
                            Spacer(Modifier.width(12.dp))
                            Text("Hạn thanh toán", style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF607D8B)))
                        }
                        Text(if (dueText.isNotBlank()) dueText else "—", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1F2933)))
                    }
                    
                    // Kỳ ghi chỉ số
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = Color(0xFFE8F5E9), modifier = Modifier.size(28.dp)) {
                                Box(contentAlignment = Alignment.Center) { Icon(Icons.Filled.Receipt, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(14.dp)) }
                            }
                            Spacer(Modifier.width(12.dp))
                            Text("Kỳ ghi chỉ số", style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF607D8B)))
                        }
                        Text(periodText, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1F2933)))
                    }
                }
                
                Spacer(Modifier.height(6.dp))
                
                // Dashed Divider 2
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
                    drawLine(
                        color = Color(0xFFCFD8DC),
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                        strokeWidth = 2f,
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(16f, 16f), 0f)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            val primaryClick = when {
                invoice == null -> onTraCuuHoaDon
                isPaid || isReplacement -> onXemChiTiet
                else -> onThanhToanNgay
            }
            val primaryLabel = when {
                invoice == null -> "Tra cứu hóa đơn"
                isPaid || isReplacement -> "Xem chi tiết"
                else -> "Thanh toán ngay"
            }
            Button(
                onClick = primaryClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPaid || isReplacement) Color(0xFFF5F7FA) else Color(0xFF1565C0),
                    contentColor = if (isPaid || isReplacement) Color(0xFF1976D2) else Color.White,
                ),
                elevation = null
            ) {
                Text(
                    text = primaryLabel, 
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
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
        BadgedBox(
            badge = {
                if (item.badge > 0) {
                    Badge(
                        containerColor = Color.Red,
                        contentColor = Color.White,
                    ) {
                        Text(text = if (item.badge > 9) "9+" else "${item.badge}")
                    }
                }
            },
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
                        modifier = Modifier.size(36.dp),
                        tint = item.iconTint,
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            ),
            color = Color(0xFF374151),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun CompanyBannerCarousel() {
    val banners = listOf(
        Res.drawable.banner_1,
        Res.drawable.banner_2,
        Res.drawable.banner_3,
        Res.drawable.banner_4,
        Res.drawable.banner_5
    )
    val pageCount = banners.size
    val pagerState = rememberPagerState(pageCount = { pageCount })

    LaunchedEffect(Unit) {
        while (true) {
            yield()
            delay(3000)
            pagerState.animateScrollToPage(
                page = (pagerState.currentPage + 1) % pagerState.pageCount,
                animationSpec = tween(600)
            )
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().height(280.dp),
        ) { page ->
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(0.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(banners[page]),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Dấu chấm tròn chỉ báo (Dots Indicator)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pageCount) { iteration ->
                val isSelected = pagerState.currentPage == iteration
                val color = if (isSelected) bannerBlue else Color(0xFFE0E0E0)
                val width = if (isSelected) 16.dp else 8.dp
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .height(8.dp)
                        .width(width)
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
