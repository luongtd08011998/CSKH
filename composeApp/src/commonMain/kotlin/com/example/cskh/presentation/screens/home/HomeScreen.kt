package com.example.cskh.presentation.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.draw.clipToBounds
import org.jetbrains.compose.resources.DrawableResource
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import com.example.cskh.presentation.components.bounceClick
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
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.CalendarMonth
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
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.setValue
import coil3.compose.AsyncImage
import com.example.cskh.presentation.screens.invoices.buildVietQrQuickLinkUrl
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.example.cskh.presentation.screens.invoices.buildVietQrQuickLinkUrl
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.platform.UriHandler
import com.example.cskh.util.formatVnd
import org.jetbrains.compose.resources.painterResource
import cskh.composeapp.generated.resources.Res
import cskh.composeapp.generated.resources.logocty1
import cskh.composeapp.generated.resources.banner_1
import cskh.composeapp.generated.resources.banner_2
import cskh.composeapp.generated.resources.banner_4
import cskh.composeapp.generated.resources.Baoduong2
import cskh.composeapp.generated.resources.NhaMayXuLiNuoc

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
    onNavigateCustomerDocuments: () -> Unit = onNavigateCustomerProfile,
    onNavigateWaterPrice: () -> Unit,
    onNavigateAbout: () -> Unit,
    onNavigateOther: () -> Unit,
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
            title = "Báo cáo sự cố",
            subtitle = "Gửi báo cáo",
            icon = Icons.Filled.Feedback,
            iconBackground = Color(0xFFFFF3E0), // orange-100
            iconTint = Color(0xFFEF6C00), // orange-600
            badge = feedbackUnreadCount,
            onClick = onNavigateFeedbackNotifications,
        ),
        HomeServiceCard(
            title = "Hồ sơ khách hàng",
            subtitle = "Xem tệp hồ sơ PDF",
            icon = Icons.Filled.FolderShared,
            iconBackground = Color(0xFFE0F2FE), // sky-100
            iconTint = Color(0xFF0284C7), // sky-600
            onClick = onNavigateCustomerDocuments,
        ),
        HomeServiceCard(
            title = "Khác",
            subtitle = "Xem thêm",
            icon = Icons.Filled.MoreHoriz,
            iconBackground = Color(0xFFF3E8FF), // purple-100
            iconTint = Color(0xFF9333EA), // purple-600
            onClick = onNavigateOther,
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


                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-20).dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                            ) {
                                val newest = state.recentInvoices.firstOrNull()
                                var qrDetail by remember { mutableStateOf<InvoiceDetail?>(null) }
                                qrDetail?.let { d ->
                                    Dialog(onDismissRequest = { qrDetail = null }) {
                                        Surface(shape = RoundedCornerShape(20.dp), color = Color.White) {
                                            Column(
                                                modifier = Modifier.padding(20.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                            ) {
                                                Text(
                                                    "Mã QR thanh toán",
                                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = Color(0xFF1F2933),
                                                )
                                                Text(
                                                    "Quét bằng ứng dụng ngân hàng bất kỳ",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color(0xFF757575),
                                                )
                                                Spacer(Modifier.height(12.dp))
                                                AsyncImage(
                                                    model = remember(d.id, d.totalAmount) { buildVietQrQuickLinkUrl(d) },
                                                    contentDescription = "Mã QR VietQR thanh toán",
                                                    modifier = Modifier.fillMaxWidth(),
                                                )
                                                Spacer(Modifier.height(12.dp))
                                                Button(
                                                    onClick = { qrDetail = null },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                                                ) { Text("Đóng", color = Color.White) }
                                            }
                                        }
                                    }
                                }
                                CurrentInvoiceCard(
                                    invoice = newest,
                                    detail = state.currentInvoiceDetail,
                                    onThanhToanNgay = {
                                        val d = state.currentInvoiceDetail
                                        if (d != null) qrDetail = d
                                        else newest?.id?.let { onNavigateInvoiceDetail(it) } ?: onNavigateInvoices()
                                    },
                                    onXemChiTiet = {
                                        newest?.id?.let { onNavigateInvoiceDetail(it) } ?: onNavigateInvoices()
                                    },
                                    onTraCuuHoaDon = onNavigateInvoices,
                                    )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            SectionHeader(
                                title = "Dịch vụ tiện ích",
                                modifier = Modifier.padding(horizontal = 20.dp),
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                MenuGrid(items = menu)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            SectionHeader(
                                title = "Thông tin & Hoạt động",
                                modifier = Modifier.padding(horizontal = 20.dp),
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            CompanyBannerCarousel()
                            Spacer(modifier = Modifier.height(16.dp))
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
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
        shadowElevation = 4.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(Color(0xFF0D47A1), Color(0xFF0288D1))))
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top: Logo + Company Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Surface(
                        modifier = Modifier.size(46.dp),
                        shape = CircleShape,
                        color = Color.Transparent,
                        shadowElevation = 3.dp,
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.logocty1),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                        )
                    }
                    Column {
                        Text(
                            text = "CÔNG TY TNHH",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.85f),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp,
                            ),
                        )
                        Text(
                            text = companyName.replace("Công ty TNHH ", ""),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 17.sp,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Customer Glassmorphism Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bounceClick(onClick = onOpenProfile),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Avatar Icon Circle
                        Surface(
                            modifier = Modifier.size(38.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.22f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        // Info Column
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Xin chào,",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontWeight = FontWeight.Normal,
                                    )
                                )
                                Text(
                                    text = customer?.name ?: "—",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.White.copy(alpha = 0.18f),
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "Mã KH:",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.5.sp,
                                                color = Color.White.copy(alpha = 0.85f),
                                                fontWeight = FontWeight.Medium
                                            )
                                        )
                                        Text(
                                            text = customer?.digiCode ?: "—",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFFD54F) // Vàng gold nhẹ nổi bật
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // Arrow forward indicator
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.18f),
                            modifier = Modifier.size(28.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Xem thông tin",
                                    tint = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
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
    
    // Hạn thanh toán: Ngày phát hành + 14 ngày (nếu không có thì lấy endDate hoặc "—")
    val dueText = remember(detail?.createdDate, detail?.endDate) {
        detail?.createdDate.addDays(14)
            ?: detail?.endDate?.takeIf { it.isNotBlank() }?.toDateDisplay()
            ?: "—"
    }

    var showQr by remember { mutableStateOf(false) }
    if (showQr && detail != null) {
        val qrUrl = remember(detail.id, detail.totalAmount) { buildVietQrQuickLinkUrl(detail) }
        val qrPngSaver = koinInject<com.example.cskh.platform.QrPngSaver>()
        val qrScope = androidx.compose.runtime.rememberCoroutineScope()
        var isSaving by remember { mutableStateOf(false) }
        var saveMsg by remember { mutableStateOf<String?>(null) }
        var saveMsgIsError by remember { mutableStateOf(false) }
        Dialog(
            onDismissRequest = { showQr = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                shadowElevation = 12.dp,
            ) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    // Header gradient
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(heroGradient)
                            .padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 52.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Quét mã để thanh toán",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                                color = Color.White.copy(alpha = 0.85f),
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = (invoice?.totalAmount ?: detail.totalAmount)?.formatVnd().orEmpty(),
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = Color.White,
                            )
                            if (monthText.isNotBlank()) {
                                Spacer(Modifier.height(8.dp))
                                Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.18f)) {
                                    Text(
                                        "Hóa đơn tháng $monthText",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = Color.White,
                                    )
                                }
                            }
                        }
                    }

                    // QR card overlaps header
                    Surface(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .offset(y = (-34).dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE3EAF3)),
                        shadowElevation = 6.dp,
                    ) {
                        AsyncImage(
                            model = qrUrl,
                            contentDescription = "Mã QR VietQR thanh toán",
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                        )
                    }

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .offset(y = (-22).dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            "Mở ứng dụng ngân hàng và quét mã",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF78909C),
                            textAlign = TextAlign.Center,
                        )
                        saveMsg?.let { msg ->
                            Spacer(Modifier.height(6.dp))
                            Text(
                                msg,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = if (saveMsgIsError) Color(0xFFC62828) else Color(0xFF2E7D32),
                                textAlign = TextAlign.Center,
                            )
                        }
                        Spacer(Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Button(
                                onClick = { showQr = false },
                                modifier = Modifier.weight(1f).height(48.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                shape = CircleShape,
                                elevation = null,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF1F4F8),
                                    contentColor = Color(0xFF455A64),
                                ),
                            ) {
                                Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Đóng", maxLines = 1, fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = {
                                    if (isSaving) return@Button
                                    qrScope.launch {
                                        isSaving = true
                                        saveMsg = null
                                        qrPngSaver.savePngFromUrl(qrUrl).fold(
                                            onSuccess = { saveMsg = it; saveMsgIsError = false },
                                            onFailure = { e -> saveMsg = e.message ?: "Không lưu được ảnh"; saveMsgIsError = true },
                                        )
                                        isSaving = false
                                    }
                                },
                                enabled = !isSaving,
                                modifier = Modifier.weight(1.5f).height(48.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1565C0),
                                    contentColor = Color.White,
                                ),
                            ) {
                                Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(if (isSaving) "Đang tải..." else "Tải về máy", maxLines = 1, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }
        }

    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.5.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp).fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (monthText.isNotBlank()) "Hóa đơn tháng $monthText" else "Hóa đơn tháng hiện tại",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                    color = Color(0xFF607D8B),
                )
                if (invoice != null) {
                    Surface(
                        color = statusBg,
                        shape = RoundedCornerShape(999.dp),
                    ) {
                        Text(
                            text = statusText,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 10.5.sp),
                            color = statusColor,
                        )
                    }
                }
            }

            Spacer(Modifier.height(2.dp))

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = invoice?.totalAmount?.formatVnd()?.replace(" đ", "₫")?.replace(" VNĐ", "₫") ?: "—",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2), // Primary Blue
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (invoice != null) {
                Spacer(Modifier.height(6.dp))

                // Dashed Divider
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
                    drawLine(
                        color = Color(0xFFECEFF1),
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                        strokeWidth = 2f,
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                    )
                }

                Spacer(Modifier.height(6.dp))

                // 2 Cột thông tin: Lượng nước & Hạn thanh toán
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Cột trái: Lượng nước tiêu thụ
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Surface(shape = CircleShape, color = Color(0xFFE3F2FD), modifier = Modifier.size(22.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.WaterDrop, contentDescription = null, tint = Color(0xFF1976D2), modifier = Modifier.size(12.dp))
                            }
                        }
                        Column {
                            Text("Lượng nước", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF78909C), fontSize = 10.sp))
                            Text("${kotlin.math.max(invoice.newVal - invoice.oldVal, 0)} m³", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = Color(0xFF1F2933)))
                        }
                    }

                    // Cột phải: Hạn thanh toán
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Surface(shape = CircleShape, color = Color(0xFFFFF3E0), modifier = Modifier.size(22.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(12.dp))
                            }
                        }
                        Column {
                            Text("Hạn thanh toán", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF78909C), fontSize = 10.sp))
                            Text(if (dueText.isNotBlank()) dueText else "—", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = Color(0xFF1F2933)))
                        }
                    }
                }
            }

            Spacer(Modifier.height(7.dp))

            val primaryClick = when {
                invoice == null -> onTraCuuHoaDon
                isPaid || isReplacement -> onXemChiTiet
                detail != null -> ({ showQr = true })
                else -> onThanhToanNgay
            }
            val primaryLabel = when {
                invoice == null -> "Tra cứu hóa đơn"
                isPaid || isReplacement -> "Xem chi tiết"
                else -> "Thanh toán ngay"
            }

            Button(
                onClick = primaryClick,
                modifier = Modifier.fillMaxWidth().height(34.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPaid || isReplacement) Color(0xFFF5F7FA) else Color(0xFF1565C0),
                    contentColor = if (isPaid || isReplacement) Color(0xFF1976D2) else Color.White,
                ),
                elevation = null,
            ) {
                Text(
                    text = primaryLabel,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 12.5.sp),
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
    } else if (Regex("\\d{8}").matches(s.take(8))) {
        "${s.substring(6, 8)}/${s.substring(4, 6)}/${s.substring(0, 4)}"
    } else s
}

private val DAYS_IN_MONTH = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

private fun isLeapYear(y: Int) = (y % 4 == 0 && y % 100 != 0) || (y % 400 == 0)

private fun daysIn(y: Int, m: Int) = if (m == 2 && isLeapYear(y)) 29 else DAYS_IN_MONTH[m - 1]

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

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(bannerBlue),
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            ),
            color = Color(0xFF1E293B),
        )
    }
}

@Composable
private fun MenuGrid(items: List<HomeServiceCard>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top,
        ) {
            items.forEach { item ->
                MenuIconCard(
                    modifier = Modifier.weight(1f),
                    item = item,
                )
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
            .bounceClick(onClick = item.onClick)
            .padding(horizontal = 2.dp, vertical = 2.dp),
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
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(14.dp),
                color = item.iconBackground,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = item.iconTint,
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.5.sp,
                lineHeight = 14.sp,
            ),
            color = Color(0xFF374151),
            textAlign = TextAlign.Center,
            maxLines = 2,
            minLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private data class CompanyBanner(
    val image: DrawableResource,
    val title: String,
    val subtitle: String,
)

private const val BannerAutoScrollMs = 4000L

@Composable
private fun CompanyBannerCarousel() {
    val banners = remember {
        listOf(
            CompanyBanner(Res.drawable.banner_1, "Trụ sở Công ty", "Ấp Tóc Tiên 1, Xã Châu Pha, Thành phố Hồ Chí Minh, Việt Nam."),
            CompanyBanner(Res.drawable.NhaMayXuLiNuoc, "Nhà máy xử lý nước", "Toàn cảnh hệ thống từ trên cao"),
            CompanyBanner(Res.drawable.Baoduong2, "Bảo dưỡng mạng lưới", "Duy trì & kiểm tra hệ thống đường ống an toàn"),
            CompanyBanner(Res.drawable.banner_2, "Vị trí Công ty", "Tuyến đường Phú Mỹ - Tóc Tiên"),
            CompanyBanner(Res.drawable.banner_4, "Phục vụ khu vực Phú Mỹ", "Cung cấp nước sạch cho doanh nghiệp & dân cư"),
        )
    }
    val pageCount = banners.size
    val pagerState = rememberPagerState(pageCount = { pageCount })
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    // Tự chuyển trang; tạm dừng khi người dùng đang vuốt, đếm lại từ đầu sau mỗi lần đổi trang
    LaunchedEffect(pagerState.settledPage, isDragged) {
        if (isDragged) return@LaunchedEffect
        delay(BannerAutoScrollMs)
        yield()
        pagerState.animateScrollToPage(
            page = (pagerState.currentPage + 1) % pageCount,
            animationSpec = tween(800),
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().clipToBounds(),
        ) { page ->
            val banner = banners[page]

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clipToBounds(),
            ) {
                Image(
                    painter = painterResource(banner.image),
                    contentDescription = banner.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )

                // Lớp phủ gradient để chữ trắng dễ đọc
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0f to Color.Transparent,
                                0.45f to Color.Transparent,
                                1f to Color.Black.copy(alpha = 0.75f),
                            ),
                        ),
                )

                // Chip số trang
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                    shape = RoundedCornerShape(50),
                    color = Color.Black.copy(alpha = 0.45f),
                ) {
                    Text(
                        text = "${page + 1}/$pageCount",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                    )
                }

                // Chú thích
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    Text(
                        text = banner.title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 15.5.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = banner.subtitle,
                        color = Color.White.copy(alpha = 0.88f),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Dấu chấm tròn chỉ báo (Dots Indicator) có hiệu ứng smooth
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pageCount) { iteration ->
                val isSelected = pagerState.currentPage == iteration
                val color by animateColorAsState(
                    targetValue = if (isSelected) bannerBlue else Color(0xFFE0E0E0),
                    animationSpec = tween(300)
                )
                val width by animateDpAsState(
                    targetValue = if (isSelected) 24.dp else 8.dp,
                    animationSpec = tween(300)
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
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
                    color = Color.Transparent,
                    shadowElevation = 4.dp,
                ) {
                    Image(
                        painter = painterResource(Res.drawable.logocty1),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
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
            .bounceClick(onClick = onClick),
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
            .bounceClick(onClick = item.onClick),
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
