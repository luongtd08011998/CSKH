package com.example.cskh.presentation.screens.static

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private val pageBgTop = Color(0xFFFAFAFA)
private val pageBgBottom = Color(0xFFF0F0F0)
private val headerBlueStart = Color(0xFF42A5F5)
private val headerBlueEnd = Color(0xFF1E88E5)

private data class PriceTier(
    val title: String,
    val oldPrice: String? = null,
    val newPrice: String? = null,
)

private data class PriceGroup(
    val code: String,
    val name: String,
    val tiers: List<PriceTier>,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterPriceScreen(onBack: () -> Unit) {
    val groups = rememberWaterPriceGroups()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bảng giá nước") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
            )
        },
        containerColor = Color.Transparent,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = listOf(pageBgTop, pageBgBottom)))
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            HeaderWaterPrice()

            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "BẢNG GIÁ NƯỚC ÁP DỤNG TỪ 01/01/2024",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = Color(0xFF212121),
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        PriceTableHeader()
                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(color = Color(0xFFEAEAEA))
                        Spacer(modifier = Modifier.height(10.dp))

                        groups.forEachIndexed { index, g ->
                            PriceGroupCard(g)
                            if (index != groups.lastIndex) Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Ghi chú",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = Color(0xFF212121),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "(*) Quyết định số 1486/QĐ-UBND của UBND Tỉnh Bà Rịa - Vũng Tàu.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF424242),
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "(**) Thông báo số 61/TB_CNTT của Công ty TNHH Cấp nước Tóc Tiên.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF424242),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderWaterPrice() {
    Surface(modifier = Modifier.fillMaxWidth(), color = Color.Transparent) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(colors = listOf(headerBlueStart, headerBlueEnd)))
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    modifier = Modifier.size(52.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.WaterDrop,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Bảng giá nước",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                        ),
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Giá cũ / giá mới (VNĐ/m³) theo nhóm đối tượng",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                    )
                }
                Icon(
                    Icons.Filled.AttachMoney,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.95f),
                    modifier = Modifier.size(28.dp),
                )
            }
        }
    }
}

@Composable
private fun PriceTableHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Đối tượng / Mức sử dụng",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = Color(0xFF424242),
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "Giá cũ\n(VNĐ/m³)",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF757575),
            textAlign = TextAlign.End,
            modifier = Modifier.widthIn(min = 78.dp),
        )
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = "Giá mới\n(VNĐ/m³)",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF757575),
            textAlign = TextAlign.End,
            modifier = Modifier.widthIn(min = 78.dp),
        )
    }
}

@Composable
private fun PriceGroupCard(group: PriceGroup) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFF7FAFF),
        border = BorderStroke(1.dp, Color(0xFFE3F2FD)),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "${group.code}. ${group.name}",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = Color(0xFF1565C0),
            )
            Spacer(modifier = Modifier.height(8.dp))
            group.tiers.forEachIndexed { idx, tier ->
                PriceTierRow(tier)
                if (idx != group.tiers.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun PriceTierRow(tier: PriceTier) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = tier.title,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF212121),
            modifier = Modifier.weight(1f),
        )
        Text(
            text = tier.oldPrice ?: "—",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = Color(0xFF424242),
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 10.dp),
        )
        Text(
            text = tier.newPrice ?: "—",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Color(0xFF1976D2),
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 14.dp),
        )
    }
}

@Composable
private fun rememberWaterPriceGroups(): List<PriceGroup> {
    // Data mirrored from docs/banggianuoc.md (normalized to structured tiers).
    return listOf(
        PriceGroup(
            code = "I",
            name = "Nước sinh hoạt đồng bào dân tộc",
            tiers = listOf(
                PriceTier("Từ 0 – 10m³/ đồng hồ/tháng", oldPrice = "4.500", newPrice = "5.500"),
                PriceTier("Từ trên 10 - 20m³/ đồng hồ/tháng", oldPrice = null, newPrice = "11.000"),
                PriceTier("Từ trên 20m³/ đồng hồ/tháng", oldPrice = null, newPrice = "12.000"),
            ),
        ),
        PriceGroup(
            code = "II",
            name = "Nước sinh hoạt nông thôn",
            tiers = listOf(
                PriceTier("Từ 0 – 10m³/ đồng hồ/tháng", oldPrice = "8.400", newPrice = "9.300"),
                PriceTier("Từ trên 10 - 20m³/ đồng hồ/tháng", oldPrice = "11.000", newPrice = "11.000"),
                PriceTier("Từ trên 20m³/ đồng hồ/tháng", oldPrice = null, newPrice = "12.500"),
            ),
        ),
        PriceGroup(
            code = "III",
            name = "Nước sinh hoạt đô thị",
            tiers = listOf(
                PriceTier("Từ 0 – 10m³/ đồng hồ/tháng", oldPrice = "8.500", newPrice = "9.400"),
                PriceTier("Từ trên 10 - 20m³/ đồng hồ/tháng", oldPrice = "12.500", newPrice = "12.600"),
                PriceTier("Từ trên 20m³/ đồng hồ/tháng", oldPrice = null, newPrice = "13.500"),
            ),
        ),
        PriceGroup(
            code = "IV",
            name = "Hành chính sự nghiệp",
            tiers = listOf(
                PriceTier("Áp dụng chung", oldPrice = "12.500", newPrice = "13.100"),
            ),
        ),
        PriceGroup(
            code = "V",
            name = "Sản xuất vật chất",
            tiers = listOf(
                PriceTier("Bán trực tiếp cho khách hàng", oldPrice = "12.500", newPrice = "13.400"),
                PriceTier("Bán qua đồng hồ tổng KCN", oldPrice = "11.500", newPrice = "12.400"),
            ),
        ),
        PriceGroup(
            code = "VI",
            name = "Kinh doanh dịch vụ",
            tiers = listOf(
                PriceTier("Áp dụng chung", oldPrice = "19.000", newPrice = "20.200"),
            ),
        ),
    )
}
