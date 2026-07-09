package com.example.cskh.presentation.screens.invoices

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import com.example.cskh.presentation.components.bounceClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cskh.domain.model.InvoiceDisplayType
import com.example.cskh.domain.model.InvoiceSummary
import com.example.cskh.domain.model.ProcessedInvoice
import com.example.cskh.util.formatVnd
import org.koin.compose.viewmodel.koinViewModel

// ── Color palette ────────────────────────────────────────────────────────────
private val pageBackground = Color(0xFFF0F4F8)
private val paidGreen      = Color(0xFF2E7D32)
private val unpaidOrange   = Color(0xFFE65100)
private val tableHeaderBg  = Color(0xFF1976D2)
private val replaceBlue    = Color(0xFF1565C0)
private val replacedGray   = Color(0xFF9E9E9E)
private val chartLineColor = Color(0xFF1976D2)
private val chartFillStart = Color(0x661976D2)
private val chartFillEnd   = Color(0x001976D2)

// ── Helper extensions ────────────────────────────────────────────────────────
private fun InvoiceSummary.isPaid(): Boolean =
    paymentStatusLabel.contains("đã thanh toán", ignoreCase = true)

private fun InvoiceSummary.isUnpaid(): Boolean =
    paymentStatusLabel.contains("chưa thanh toán", ignoreCase = true)

private fun InvoiceSummary.waterUsed(): Int = (newVal - oldVal).coerceAtLeast(0)

/** Parse yearMonth "YYYY/MM" hoặc "YYYYMM" -> display "M/YYYY" */
private fun String.toMonthDisplay(): String {
    // Format "YYYY/MM"
    val parts = this.split("/")
    if (parts.size == 2) {
        val month = parts[1].trimStart('0').ifEmpty { "0" }
        return "$month/${parts[0]}"
    }
    // Format "YYYYMM" (6 ký tự liền, không dấu /)
    if (this.length == 6 && this.all { it.isDigit() }) {
        val year  = this.substring(0, 4)
        val month = this.substring(4, 6).trimStart('0').ifEmpty { "0" }
        return "$month/$year"
    }
    return this
}

/** Format number with thousand separators: 1708090 -> "1,708,090" */
private fun Double.formatCompact(): String {
    val long = this.toLong()
    val s = long.toString()
    return buildString {
        var count = 0
        for (i in s.indices.reversed()) {
            if (count > 0 && count % 3 == 0) insert(0, ',')
            insert(0, s[i])
            count++
        }
    }
}

private fun formatYLabel(value: Float): String =
    "${value.toInt()}m³"


// ── Screen ───────────────────────────────────────────────────────────────────
@Composable
fun InvoiceListScreen(
    onBack: () -> Unit,
    onOpenDetail: (Long) -> Unit,
    onLogout: () -> Unit,
    viewModel: InvoiceListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    if (state.sessionExpired) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { },
            title = { Text("Phiên đăng nhập hết hạn") },
            text = { Text("Phiên làm việc của bạn đã hết hạn. Vui lòng đăng nhập lại để tiếp tục sử dụng.") },
            confirmButton = {
                Button(onClick = {
                    viewModel.acknowledgeSessionExpired()
                    onLogout()
                }) { Text("OK") }
            }
        )
    }

    // Rows: Normal + Replacement only (hide Replaced)
    val tableItems = remember(state.items) {
        state.items.filter { it.displayType != InvoiceDisplayType.Replaced }
    }

    // Biểu đồ: group theo kỳ, cộng sản lượng nếu cùng kỳ có nhiều HĐ, lấy 12 kỳ gần nhất
    val chartData = remember(state.items) {
        state.items
            .filter { it.displayType != InvoiceDisplayType.Replaced }
            .map { it.invoice }
            .sortedBy { it.yearMonth }
            .groupBy { it.yearMonth }                       // group theo kỳ
            .map { (yearMonth, invoices) ->
                Pair(yearMonth, invoices.sumOf { it.waterUsed() }) // cộng sản lượng
            }
            .sortedBy { it.first }
            .takeLast(12)
    }

    Scaffold(
        containerColor = pageBackground,
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 4.dp,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White,
                        )
                    }
                    Text(
                        text = "Hóa đơn tiền nước",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        ),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when {
                state.isLoading && state.items.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                else -> {
                    val listState = rememberLazyListState()
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        // Customer info card
                        if (tableItems.isNotEmpty()) {
                            val firstInvoice = tableItems.first().invoice
                            item {
                                CustomerInfoCard(
                                    digiCode = firstInvoice.digiCode,
                                    customerName = firstInvoice.customerName,
                                )
                            }
                        }

                        // Area chart
                        if (chartData.size >= 2) {
                            item {
                                InvoiceAreaChart(data = chartData)
                            }
                        }

                        // Table
                        item {
                            InvoiceTable(
                                items = tableItems,
                                onOpenDetail = onOpenDetail,
                            )
                        }

                        // Load more indicator
                        if (state.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    contentAlignment = Alignment.Center,
                                ) { CircularProgressIndicator() }
                            }
                        }

                        val meta = state.meta
                        if (meta != null && state.currentPage < meta.pages && !state.isLoadingMore) {
                            item {
                                Button(
                                    onClick = { viewModel.loadMore() },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !state.isLoading && !state.isLoadingMore,
                                ) { Text("Tải thêm") }
                            }
                        }
                    }
                }
            }

            state.errorMessage?.let { msg ->
                Surface(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        text = msg,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

// ── Customer Info Card ────────────────────────────────────────────────────────
@Composable
private fun CustomerInfoCard(
    digiCode: String,
    customerName: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        Icons.Default.Badge,
                        contentDescription = null,
                        tint = tableHeaderBg,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        "Mã khách hàng",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    )
                }
                Text(
                    text = digiCode,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFEEEEEE))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = tableHeaderBg,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        "Tên khách hàng",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    )
                }
                Text(
                    text = customerName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

// ── Area Chart ────────────────────────────────────────────────────────────────
@Composable
private fun InvoiceAreaChart(data: List<Pair<String, Int>>) {
    val textMeasurer = rememberTextMeasurer()
    
    val animationProgress = remember { androidx.compose.animation.core.Animatable(0f) }
    androidx.compose.runtime.LaunchedEffect(data) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = androidx.compose.animation.core.tween(
                durationMillis = 2000, 
                easing = androidx.compose.animation.core.FastOutSlowInEasing
            )
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)) {
            val amounts = data.map { it.second.toFloat() }
            val maxVal = amounts.maxOrNull()?.coerceAtLeast(1f) ?: 1f
            val minVal = 0f
            val range = maxVal - minVal
            val labels = data.map { it.first }

            Box(modifier = Modifier.fillMaxWidth().padding(start = 4.dp, end = 8.dp)) {
                // Y-axis labels
                Column(
                    modifier = Modifier.align(Alignment.TopStart).padding(start = 4.dp, top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    listOf(maxVal, maxVal * 0.66f, maxVal * 0.33f, 0f).forEach { v ->
                        Text(
                            text = formatYLabel(v),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = Color(0xFF9E9E9E),
                            modifier = Modifier.height(40.dp),
                        )
                    }
                }

                // Chart + X-axis labels in one Canvas (no clip issues)
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)           // 160dp chart + 50dp label area
                        .padding(start = 36.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
                ) {
                    val chartH = 155.dp.toPx()    // chart drawing area
                    
                    clipRect(right = size.width * animationProgress.value) {
                        drawAreaChart(amounts, maxVal, minVal, range, chartH)
                    }
                    
                    drawXAxisLabels(labels, textMeasurer, chartH)
                }
            }
        }
    }
}

private fun DrawScope.drawAreaChart(
    amounts: List<Float>,
    maxVal: Float,
    minVal: Float,
    range: Float,
    chartH: Float,
) {
    if (amounts.size < 2) return
    val w = size.width
    val n = amounts.size
    // Inset edge-to-edge: điểm đầu trùng với trục Y (leftInset = 0), điểm cuối cách phải 20dp
    val leftInset  = 0f
    val rightInset = 20.dp.toPx()
    val usable = w - leftInset - rightInset
    val stepX  = if (n > 1) usable / (n - 1).toFloat() else usable

    fun xOf(i: Int) = leftInset + i * stepX
    fun yOf(v: Float) = chartH - ((v - minVal) / range * chartH).coerceIn(0f, chartH)

    val linePath = Path()
    val fillPath = Path()

    linePath.moveTo(xOf(0), yOf(amounts[0]))
    fillPath.moveTo(xOf(0), chartH)
    fillPath.lineTo(xOf(0), yOf(amounts[0]))

    for (i in 1 until amounts.size) {
        val x0 = xOf(i - 1); val y0 = yOf(amounts[i - 1])
        val x1 = xOf(i);     val y1 = yOf(amounts[i])
        val cx = (x0 + x1) / 2f
        linePath.cubicTo(cx, y0, cx, y1, x1, y1)
        fillPath.cubicTo(cx, y0, cx, y1, x1, y1)
    }

    fillPath.lineTo(xOf(amounts.size - 1), chartH)
    fillPath.close()

    drawPath(
        path = fillPath,
        brush = Brush.verticalGradient(
            colors = listOf(chartFillStart, chartFillEnd),
            startY = 0f,
            endY = chartH,
        ),
    )

    drawPath(
        path = linePath,
        color = chartLineColor,
        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
    )

    for (i in amounts.indices) {
        val cx = xOf(i)
        val cy = yOf(amounts[i])
        drawCircle(color = chartLineColor.copy(alpha = 0.22f), radius = 8.dp.toPx(), center = Offset(cx, cy))
        drawCircle(color = chartLineColor, radius = 5.5.dp.toPx(), center = Offset(cx, cy))
        drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = Offset(cx, cy))
    }
}

private fun DrawScope.drawXAxisLabels(
    labels: List<String>,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    chartH: Float,
) {
    if (labels.isEmpty()) return
    val w = size.width
    val n = labels.size
    // Cùng inset với drawAreaChart để nhãn khớp điểm
    val leftInset  = 0f
    val rightInset = 20.dp.toPx()
    val usable = w - leftInset - rightInset
    val stepX  = if (n > 1) usable / (n - 1).toFloat() else usable
    val labelY = chartH + 6.dp.toPx()
    val labelStyle = TextStyle(fontSize = 8.sp, color = Color(0xFF546E7A))

    for (i in labels.indices) {
        val measured = textMeasurer.measure(text = labels[i], style = labelStyle)
        // Dịch trục X của chữ sang trái bằng (độ dài chữ * cos(45°))
        // để khi xoay nghiêng -45°, điểm KẾT THÚC của chữ sẽ thẳng hàng dọc với chấm tròn
        val textWidth = measured.size.width.toFloat()
        val cx = leftInset + i * stepX - (textWidth * 0.707f)
        
        withTransform({
            translate(cx, labelY)
            rotate(degrees = -45f, pivot = Offset.Zero)
        }) {
            drawText(measured)
        }
    }
}

// ── Invoice Table ─────────────────────────────────────────────────────────────
@Composable
private fun InvoiceTable(
    items: List<ProcessedInvoice>,
    onOpenDetail: (Long) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column {
            // Legend
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    "Diễn giải:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Icon(Icons.Default.CheckCircle, null, tint = paidGreen, modifier = Modifier.size(16.dp))
                    Text("Đã thanh toán", style = MaterialTheme.typography.labelSmall.copy(color = paidGreen))
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Icon(Icons.Default.Cancel, null, tint = unpaidOrange, modifier = Modifier.size(16.dp))
                    Text("Chưa thanh toán", style = MaterialTheme.typography.labelSmall.copy(color = unpaidOrange))
                }
            }

            // Header row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(tableHeaderBg)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Kỳ",
                    modifier = Modifier.weight(1.2f),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp,
                    ),
                )
                Text(
                    "Tiêu thụ\n(m³)",
                    modifier = Modifier.weight(1.3f),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp,
                    ),
                )
                Text(
                    "Tổng tiền\n(VND)",
                    modifier = Modifier.weight(2f),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp,
                    ),
                )
                Text(
                    "Trạng thái",
                    modifier = Modifier.weight(0.9f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp,
                    ),
                )
                Spacer(Modifier.width(24.dp))
            }

            // Data rows
            if (items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "Không có hóa đơn phù hợp.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                items.forEachIndexed { idx, processed ->
                    StaggeredListItem(index = idx) {
                        Column {
                            InvoiceTableRow(
                                processed = processed,
                                isEven = idx % 2 == 0,
                                onOpenDetail = onOpenDetail,
                            )
                            if (idx < items.lastIndex) {
                                HorizontalDivider(color = Color(0xFFEEEEEE))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StaggeredListItem(
    index: Int,
    content: @Composable () -> Unit
) {
    val visible = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    androidx.compose.runtime.LaunchedEffect(Unit) {
        kotlinx.coroutines.delay((index * 100L).coerceAtMost(1000L))
        visible.value = true
    }
    androidx.compose.animation.AnimatedVisibility(
        visible = visible.value,
        enter = androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(500)) + 
                androidx.compose.animation.slideInVertically(
                    initialOffsetY = { 50 },
                    animationSpec = androidx.compose.animation.core.tween(500, easing = androidx.compose.animation.core.FastOutSlowInEasing)
                ),
        modifier = Modifier.fillMaxWidth()
    ) {
        content()
    }
}

@Composable
private fun InvoiceTableRow(
    processed: ProcessedInvoice,
    isEven: Boolean,
    onOpenDetail: (Long) -> Unit,
) {
    val invoice = processed.invoice
    val isReplacement = processed.displayType == InvoiceDisplayType.Replacement
    val rowBg = if (isEven) Color.White else Color(0xFFF5F8FC)
    val paid = invoice.isPaid()

    val statusColor = when {
        isReplacement -> replaceBlue
        paid          -> paidGreen
        else          -> unpaidOrange
    }
    val statusIcon = if (paid || isReplacement) Icons.Default.CheckCircle else Icons.Default.Cancel

    val amountDisplay = if (isReplacement) "0" else invoice.totalAmount.formatCompact()
    val periodDisplay = invoice.yearMonth
    val waterUsed = invoice.waterUsed()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBg)
            .bounceClick { onOpenDetail(invoice.id) }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = periodDisplay,
            modifier = Modifier.weight(1.2f),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
        )
        Text(
            text = waterUsed.toString(),
            modifier = Modifier.weight(1.3f),
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            text = amountDisplay,
            modifier = Modifier.weight(2f),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Box(modifier = Modifier.weight(0.9f), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = statusIcon,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(20.dp),
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
            contentDescription = "Xem chi tiết",
            tint = tableHeaderBg,
            modifier = Modifier.size(22.dp),
        )
    }
}
