package com.example.cskh.presentation.screens.other

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cskh.presentation.CompanyBranding
import com.example.cskh.presentation.components.bounceClick

private val pageBackground = Color(0xFFF5F7FA)

private data class OtherMenuItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconBackground: Color,
    val iconTint: Color,
    val onClick: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherScreen(
    onBack: () -> Unit,
    onNavigateAbout: () -> Unit,
    onNavigateWaterPrice: () -> Unit,
    onNavigateRegister: () -> Unit,
    onNavigateChangeInfo: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    val menuItems = listOf(
        // Hàng 1
        OtherMenuItem(
            title = "Giới thiệu",
            subtitle = "Về chúng tôi",
            icon = Icons.Filled.Info,
            iconBackground = Color(0xFFF3E8FF), // purple-100
            iconTint = Color(0xFF9333EA),        // purple-600
            onClick = onNavigateAbout,
        ),
        OtherMenuItem(
            title = "Chất lượng nước",
            subtitle = "Kiểm tra chất lượng",
            icon = Icons.Filled.WaterDrop,
            iconBackground = Color(0xFFE0F2FE), // sky-100
            iconTint = Color(0xFF0284C7),        // sky-600
            onClick = { uriHandler.openUri("https://beta.toctienltd.vn/category/xet-nghiem-nuoc") },
        ),
        // Hàng 2
        OtherMenuItem(
            title = "Bảng giá nước",
            subtitle = "Xem bảng giá",
            icon = Icons.Filled.AttachMoney,
            iconBackground = Color(0xFFD1FAE5), // green-100
            iconTint = Color(0xFF059669),        // green-600
            onClick = onNavigateWaterPrice,
        ),
        OtherMenuItem(
            title = "Đăng ký lắp đặt",
            subtitle = "Yêu cầu lắp đặt",
            icon = Icons.Filled.AppRegistration,
            iconBackground = Color(0xFFFFF3E0), // orange-100
            iconTint = Color(0xFFEF6C00),        // orange-600
            onClick = onNavigateRegister,
        ),
        // Hàng 3
        OtherMenuItem(
            title = "Thay đổi thông tin",
            subtitle = "Cập nhật thông tin",
            icon = Icons.Filled.Edit,
            iconBackground = Color(0xFFECFDF5), // teal-50
            iconTint = Color(0xFF059669),        // teal-600
            onClick = onNavigateChangeInfo,
        ),
        OtherMenuItem(
            title = "Hotline",
            subtitle = CompanyBranding.PHONE_DISPLAY,
            icon = Icons.Filled.Phone,
            iconBackground = Color(0xFFFFE4E6), // red-100
            iconTint = Color(0xFFE11D48),        // red-600
            onClick = { uriHandler.openUri("tel:${CompanyBranding.PHONE_TEL}") },
        ),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Khác",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lai",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                ),
                windowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
            )
        },
        containerColor = pageBackground,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(pageBackground)
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    val rows = menuItems.chunked(2)
                    rows.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            row.forEach { item ->
                                OtherMenuIconCard(
                                    modifier = Modifier.weight(1f),
                                    item = item,
                                )
                            }
                            repeat(2 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun OtherMenuIconCard(
    modifier: Modifier,
    item: OtherMenuItem,
) {
    Column(
        modifier = modifier
            .wrapContentHeight()
            .bounceClick(onClick = item.onClick)
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
                    modifier = Modifier.size(36.dp),
                    tint = item.iconTint,
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            ),
            color = Color(0xFF374151),
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
        Text(
            text = item.subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF9CA3AF),
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}
