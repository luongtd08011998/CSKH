package com.example.cskh.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cskh.presentation.CompanyBranding
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler

private val pageBackground = Color(0xFFF5F7FA)
private val bannerBlue = Color(0xFF1976D2)
private val serviceCardBg = Color(0xFFF0F2F5)
private val subtitleGray = Color(0xFF757575)

private data class HomeServiceCard(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconBackground: Color,
    val onClick: () -> Unit,
)

@Composable
fun HomeScreen(
    onNavigateInvoices: () -> Unit,
    onNavigateCustomerProfile: () -> Unit,
    onNavigateWaterPrice: () -> Unit,
    onNavigateAbout: () -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uriHandler = LocalUriHandler.current
    val mapsUrl =
        "https://www.google.com/maps/search?q=" + CompanyBranding.MAP_QUERY.replace(" ", "+")
    val telUri = "tel:${CompanyBranding.PHONE_TEL}"
    val mailUri = "mailto:${CompanyBranding.EMAIL}"

    val services = listOf(
        HomeServiceCard(
            title = "Hóa đơn",
            subtitle = "Tra cứu hóa đơn",
            icon = Icons.AutoMirrored.Filled.ReceiptLong,
            iconBackground = Color(0xFF2196F3),
            onClick = onNavigateInvoices,
        ),
        HomeServiceCard(
            title = "Bảng giá nước",
            subtitle = "Xem bảng giá",
            icon = Icons.Filled.AttachMoney,
            iconBackground = Color(0xFF43A047),
            onClick = onNavigateWaterPrice,
        ),
        HomeServiceCard(
            title = "Giới thiệu",
            subtitle = "Về chúng tôi",
            icon = Icons.Filled.Info,
            iconBackground = Color(0xFF8E24AA),
            onClick = onNavigateAbout,
        ),
        HomeServiceCard(
            title = "Thông tin KH",
            subtitle = "Tài khoản của tôi",
            icon = Icons.Filled.Person,
            iconBackground = Color(0xFFFF9800),
            onClick = onNavigateCustomerProfile,
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(pageBackground),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(bannerBlue)
                .statusBarsPadding()
                .padding(vertical = 14.dp, horizontal = 8.dp),
        ) {
            Text(
                text = CompanyBranding.NAME,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(horizontal = 88.dp),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                ),
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
            TextButton(
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                modifier = Modifier.align(Alignment.CenterStart),
            ) {
                Text("Thoát", color = Color.White, style = MaterialTheme.typography.labelLarge)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
        ) {
            CompanyInfoCard(
                uriHandler = uriHandler,
                mapsUrl = mapsUrl,
                telUri = telUri,
                mailUri = mailUri,
            )

            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = "Dịch vụ",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF212121),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Chọn dịch vụ bạn cần",
                style = MaterialTheme.typography.bodyMedium,
                color = subtitleGray,
            )
            Spacer(modifier = Modifier.height(14.dp))

            services.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    rowItems.forEach { item ->
                        ServiceGridCard(
                            modifier = Modifier.weight(1f),
                            item = item,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
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
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = bannerBlue,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.WaterDrop,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = Color.White,
                        )
                    }
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                StatPill(
                    icon = Icons.Filled.Schedule,
                    line1 = "24/7",
                    line2 = "Hỗ trợ",
                )
                StatPill(
                    icon = Icons.Filled.WorkspacePremium,
                    line1 = "ISO 9001",
                    line2 = "Chứng nhận",
                )
                StatPill(
                    icon = Icons.Filled.LocalShipping,
                    line1 = "2h",
                    line2 = "Giao hàng",
                )
                StatPill(
                    icon = Icons.Filled.Shield,
                    line1 = "100%",
                    line2 = "An toàn",
                )
            }
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
