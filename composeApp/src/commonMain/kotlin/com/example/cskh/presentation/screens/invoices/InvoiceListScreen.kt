package com.example.cskh.presentation.screens.invoices

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.cskh.domain.model.InvoiceDisplayType
import com.example.cskh.domain.model.InvoiceSummary
import com.example.cskh.domain.model.ProcessedInvoice
import com.example.cskh.util.formatVnd
import org.koin.compose.viewmodel.koinViewModel

private val pageBackground = Color(0xFFF0F4F8)
private val paidGreen = Color(0xFF2E7D32)
private val unpaidOrange = Color(0xFFE65100)
private val chipBadgeBg = Color(0xFFE8EEF4)
private val replaceBlue = Color(0xFF1565C0)
private val replacedGray = Color(0xFF9E9E9E)

private fun InvoiceSummary.isPaid(): Boolean =
    paymentStatusLabel.contains("đã thanh toán", ignoreCase = true)

private fun InvoiceSummary.isUnpaid(): Boolean =
    paymentStatusLabel.contains("chưa thanh toán", ignoreCase = true)

private fun InvoiceSummary.statusColor(): Color = when {
    isPaid() -> paidGreen
    isUnpaid() -> unpaidOrange
    else -> Color(0xFF546E7A)
}

@OptIn(ExperimentalMaterial3Api::class)
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

    val listState = rememberLazyListState()
    val primary = MaterialTheme.colorScheme.primary

    val displayedItems = remember(state.items, state.searchQuery, state.paymentFilter) {
        state.items.filter { processed ->
            val item = processed.invoice
            val tabOk = when (state.paymentFilter) {
                InvoicePaymentFilter.All -> true
                InvoicePaymentFilter.Paid -> item.isPaid()
                InvoicePaymentFilter.Unpaid -> item.isUnpaid()
            }
            val q = state.searchQuery.trim()
            val searchOk = q.isEmpty() ||
                item.id.toString().contains(q) ||
                item.digiCode.contains(q, ignoreCase = true) ||
                item.customerName.contains(q, ignoreCase = true)
            tabOk && searchOk
        }
    }

    val totalAmountDisplay = remember(displayedItems) {
        displayedItems
            .filter { it.displayType != InvoiceDisplayType.Replaced }
            .sumOf { it.invoice.totalAmount }
    }

    val badgeAll = state.meta?.total ?: state.items.size
    val badgePaid = remember(state.items) { state.items.count { it.invoice.isPaid() } }
    val badgeUnpaid = remember(state.items) { state.items.count { it.invoice.isUnpaid() } }


    Scaffold(
        containerColor = pageBackground,
        topBar = {
            Surface(
                color = primary,
                shadowElevation = 4.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = viewModel::setSearchQuery,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        placeholder = {
                            Text(
                                "Tìm theo mã HĐ, mã KH, tên khách hàng…",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color.White.copy(alpha = 0.85f),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                            cursorColor = primary,
                            focusedLeadingIconColor = primary,
                            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
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
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        item {
                            SummaryCardsRow(
                                totalFormatted = totalAmountDisplay.formatVnd(),
                                orderCount = displayedItems.size,
                            )
                        }

                        item {
                            FilterChipsRow(
                                selected = state.paymentFilter,
                                onSelect = viewModel::setPaymentFilter,
                                badgeAll = badgeAll,
                                badgePaid = badgePaid,
                                badgeUnpaid = badgeUnpaid,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        if (displayedItems.isEmpty() && !state.isLoading) {
                            item {
                                Text(
                                    text = "Không có hóa đơn phù hợp.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 24.dp),
                                )
                            }
                        }

                        items(displayedItems, key = { it.invoice.id }) { item ->
                            InvoiceCard(
                                item = item,
                                onClick = { onOpenDetail(item.invoice.id) }
                            )
                        }

                        if (state.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        val meta = state.meta
                        if (meta != null && state.currentPage < meta.pages && !state.isLoadingMore) {
                            item {
                                Button(
                                    onClick = { viewModel.loadMore() },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !state.isLoading && !state.isLoadingMore,
                                ) {
                                    Text("Tải thêm")
                                }
                            }
                        }
                    }
                }
            }

            state.errorMessage?.let { msg ->
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
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

@Composable
private fun SummaryCardsRow(
    totalFormatted: String,
    orderCount: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SummaryCard(
            modifier = Modifier.weight(1f),
            title = "Tổng tiền",
            value = totalFormatted,
            icon = Icons.Default.CheckCircle,
            iconTint = paidGreen,
        )
        SummaryCard(
            modifier = Modifier.weight(1f),
            title = "Số hóa đơn",
            value = orderCount.toString(),
            icon = Icons.AutoMirrored.Filled.ReceiptLong,
            iconTint = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun SummaryCard(
    modifier: Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = iconTint,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun FilterChipsRow(
    selected: InvoicePaymentFilter,
    onSelect: (InvoicePaymentFilter) -> Unit,
    badgeAll: Int,
    badgePaid: Int,
    badgeUnpaid: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PaymentFilterChip(
            label = "Tất cả",
            selected = selected == InvoicePaymentFilter.All,
            count = badgeAll,
            selectedContainer = MaterialTheme.colorScheme.primary,
            onClick = { onSelect(InvoicePaymentFilter.All) },
        )
        PaymentFilterChip(
            label = "Đã thanh toán",
            selected = selected == InvoicePaymentFilter.Paid,
            count = badgePaid,
            selectedContainer = paidGreen,
            onClick = { onSelect(InvoicePaymentFilter.Paid) },
        )
        PaymentFilterChip(
            label = "Chưa thanh toán",
            selected = selected == InvoicePaymentFilter.Unpaid,
            count = badgeUnpaid,
            selectedContainer = unpaidOrange,
            onClick = { onSelect(InvoicePaymentFilter.Unpaid) },
        )
    }
}

@Composable
private fun PaymentFilterChip(
    label: String,
    selected: Boolean,
    count: Int,
    selectedContainer: Color,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = label,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelLarge,
                )
                Surface(
                    shape = CircleShape,
                    color = if (selected) {
                        Color.White.copy(alpha = 0.28f)
                    } else {
                        chipBadgeBg
                    },
                ) {
                    Text(
                        text = count.toString(),
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.White,
            labelColor = MaterialTheme.colorScheme.onSurface,
            selectedContainerColor = selectedContainer,
            selectedLabelColor = Color.White,
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
            selectedBorderColor = Color.Transparent,
            borderWidth = 1.dp,
        ),
    )
}

@Composable
private fun InvoiceCard(
    item: ProcessedInvoice,
    onClick: () -> Unit,
) {
    val invoice = item.invoice
    val (statusColor, pillBg, statusLabel) = when (item.displayType) {
        InvoiceDisplayType.Replacement -> Triple(
            replaceBlue,
            replaceBlue.copy(alpha = 0.14f),
            "HĐ Thay thế/Hủy",
        )
        InvoiceDisplayType.Replaced -> Triple(
            replacedGray,
            replacedGray.copy(alpha = 0.14f),
            "Đã bị thay thế",
        )
        InvoiceDisplayType.Normal -> Triple(
            invoice.statusColor(),
            invoice.statusColor().copy(alpha = 0.14f),
            invoice.paymentStatusLabel,
        )
    }

    val cardAlpha = if (item.displayType == InvoiceDisplayType.Replaced) 0.5f else 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (item.displayType == InvoiceDisplayType.Replaced) Modifier
                else Modifier.clickable {
                    onClick()
                }
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .then(Modifier.graphicsLayer { alpha = cardAlpha }),
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(12.dp)
                    .background(statusColor, CircleShape),
            )
            Spacer(modifier = Modifier.size(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = "HĐ ${invoice.id}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        )
                        Text(
                            text = invoice.customerName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    if (item.displayType != InvoiceDisplayType.Replaced) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "Chỉ số: ${invoice.oldVal} → ${invoice.newVal} m³",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "Mã KH: ${invoice.digiCode}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val amountText = if (item.displayType == InvoiceDisplayType.Replacement) {
                        "0 đ"
                    } else {
                        invoice.totalAmount.formatVnd()
                    }
                    Text(
                        text = if (item.displayType == InvoiceDisplayType.Replaced) {
                            buildAnnotatedString {
                                append(
                                    AnnotatedString(
                                        amountText,
                                        SpanStyle(textDecoration = TextDecoration.LineThrough),
                                    )
                                )
                            }
                        } else {
                            buildAnnotatedString { append(amountText) }
                        },
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = pillBg,
                    ) {
                        Text(
                            text = statusLabel,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = statusColor,
                        )
                    }
                }
            }
        }
    }
}
