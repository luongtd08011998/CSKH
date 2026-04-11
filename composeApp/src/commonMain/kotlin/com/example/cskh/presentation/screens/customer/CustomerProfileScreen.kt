package com.example.cskh.presentation.screens.customer

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cskh.domain.model.CustomerProfile
import org.koin.compose.viewmodel.koinViewModel

private val pageBackground = Color(0xFFF5F5F5)
private val headerGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF42A5F5),
        Color(0xFF1E88E5),
        Color(0xFF1565C0),
    ),
)

@Composable
fun CustomerProfileScreen(
    onBack: () -> Unit,
    viewModel: CustomerProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(pageBackground),
    ) {
        when {
            state.isLoading && state.profile == null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            state.errorMessage != null && state.profile == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        state.errorMessage ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.refresh() }) {
                        Text("Thử lại")
                    }
                }
            }

            state.profile != null -> {
                CustomerProfileScrollContent(
                    profile = state.profile!!,
                    onBack = onBack,
                    isRefreshing = state.isLoading,
                )
            }

            else -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun CustomerProfileScrollContent(
    profile: CustomerProfile,
    onBack: () -> Unit,
    isRefreshing: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(headerGradient)
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
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
                    "Thông tin cá nhân",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    ),
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = { /* placeholder */ }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.12f),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = Color(0xFF1976D2),
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                profile.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                ),
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Mã KH: ${profile.digiCode}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatusPill(
                            active = profile.isActive == 1,
                            icon = if (profile.isActive == 1) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                            label = if (profile.isActive == 1) "Đang hoạt động" else "Không hoạt động",
                            container = if (profile.isActive == 1) Color(0x332E7D32) else Color(0x33C62828),
                            content = if (profile.isActive == 1) Color(0xFFC8E6C9) else Color(0xFFFFCDD2),
                        )
                        StatusPill(
                            active = profile.isWaterCut == 0,
                            icon = Icons.Filled.WaterDrop,
                            label = if (profile.isWaterCut == 0) "Đang cung cấp" else "Đã cắt nước",
                            container = if (profile.isWaterCut == 0) Color(0x331976D2) else Color(0x33EF6C00),
                            content = if (profile.isWaterCut == 0) Color(0xFFBBDEFB) else Color(0xFFFFE0B2),
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (isRefreshing) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            ContactCard(profile)
            ExtraInfoCard(profile)
            AccountStatusCard(profile)
            SettingsMenuCard()
            UpdateButtonBar()
        }
    }
}

@Composable
private fun StatusPill(
    active: Boolean,
    icon: ImageVector,
    label: String,
    container: Color,
    content: Color,
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = container,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = content)
            Text(label, style = MaterialTheme.typography.labelMedium, color = content)
        }
    }
}

@Composable
private fun ContactCard(profile: CustomerProfile) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 4.dp,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Thông tin liên hệ",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                )
                IconButton(onClick = { /* chỉnh sửa: chưa có API */ }) {
                    Surface(shape = CircleShape, color = Color(0xFFE3F2FD)) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = null,
                            modifier = Modifier.padding(8.dp).size(20.dp),
                            tint = Color(0xFF1976D2),
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            ContactRow(
                icon = Icons.Filled.Phone,
                iconBg = Color(0xFFE3F2FD),
                iconTint = Color(0xFF1976D2),
                label = "Số điện thoại",
                value = profile.phone,
                showDivider = true,
            )
            ContactRow(
                icon = Icons.AutoMirrored.Filled.Chat,
                iconBg = Color(0xFFE8F5E9),
                iconTint = Color(0xFF388E3C),
                label = "Số điện thoại SMS",
                value = profile.sms,
                showDivider = true,
            )
            ContactRow(
                icon = Icons.Filled.Email,
                iconBg = Color(0xFFF3E5F5),
                iconTint = Color(0xFF7B1FA2),
                label = "Email",
                value = profile.email,
                emptyPlaceholder = true,
                showDivider = true,
            )
            ContactRow(
                icon = Icons.Filled.LocationOn,
                iconBg = Color(0xFFFFEBEE),
                iconTint = Color(0xFFD32F2F),
                label = "Địa chỉ",
                value = profile.address,
                showDivider = false,
            )
        }
    }
}

@Composable
private fun ContactRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    label: String,
    value: String,
    showDivider: Boolean,
    emptyPlaceholder: Boolean = false,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Surface(shape = RoundedCornerShape(12.dp), color = iconBg) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp).size(22.dp),
                    tint = iconTint,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.bodySmall, color = Color(0xFF757575))
                Spacer(modifier = Modifier.height(4.dp))
                if (emptyPlaceholder && value.isBlank()) {
                    Text(
                        "Chưa cập nhật",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFFBDBDBD),
                        ),
                    )
                } else {
                    Text(value, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF212121))
                }
            }
        }
        if (showDivider) {
            HorizontalDivider(color = Color(0xFFF0F0F0))
        }
    }
}

@Composable
private fun ExtraInfoCard(profile: CustomerProfile) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 4.dp,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Thông tin bổ sung",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            )
            Spacer(modifier = Modifier.height(12.dp))
            ContactRow(
                icon = Icons.Filled.Tag,
                iconBg = Color(0xFFE8EAF6),
                iconTint = Color(0xFF3949AB),
                label = "Mã khách hàng",
                value = profile.digiCode,
                showDivider = true,
            )
            ContactRow(
                icon = Icons.Filled.Shield,
                iconBg = Color(0xFFFFF3E0),
                iconTint = Color(0xFFE65100),
                label = "Mã số thuế",
                value = profile.taxCode,
                emptyPlaceholder = true,
                showDivider = false,
            )
        }
    }
}

@Composable
private fun AccountStatusCard(profile: CustomerProfile) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 4.dp,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Trạng thái tài khoản",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AccountStatusTile(
                    modifier = Modifier.weight(1f),
                    icon = if (profile.isActive == 1) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                    caption = "Tài khoản",
                    title = if (profile.isActive == 1) "Hoạt động" else "Không hoạt động",
                    bg = if (profile.isActive == 1) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    border = if (profile.isActive == 1) Color(0xFFA5D6A7) else Color(0xFFEF9A9A),
                    circle = if (profile.isActive == 1) Color(0xFF2E7D32) else Color(0xFFC62828),
                    titleColor = if (profile.isActive == 1) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                )
                AccountStatusTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.WaterDrop,
                    caption = "Cung cấp nước",
                    title = if (profile.isWaterCut == 0) "Bình thường" else "Đã cắt",
                    bg = if (profile.isWaterCut == 0) Color(0xFFE3F2FD) else Color(0xFFFFF3E0),
                    border = if (profile.isWaterCut == 0) Color(0xFF90CAF9) else Color(0xFFFFCC80),
                    circle = if (profile.isWaterCut == 0) Color(0xFF1976D2) else Color(0xFFEF6C00),
                    titleColor = if (profile.isWaterCut == 0) Color(0xFF0D47A1) else Color(0xFFE65100),
                )
            }
        }
    }
}

@Composable
private fun AccountStatusTile(
    modifier: Modifier,
    icon: ImageVector,
    caption: String,
    title: String,
    bg: Color,
    border: Color,
    circle: Color,
    titleColor: Color,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = bg,
        border = BorderStroke(2.dp, border),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(circle),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(caption, style = MaterialTheme.typography.bodySmall, color = Color(0xFF757575))
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = titleColor,
            )
        }
    }
}

@Composable
private fun SettingsMenuCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 4.dp,
    ) {
        Column {
            MenuRow(Icons.Filled.Notifications, Color(0xFFE3F2FD), Color(0xFF1976D2), "Thông báo")
            HorizontalDivider(color = Color(0xFFF0F0F0))
            MenuRow(Icons.Filled.Lock, Color(0xFFE8F5E9), Color(0xFF388E3C), "Bảo mật")
            HorizontalDivider(color = Color(0xFFF0F0F0))
            MenuRow(Icons.AutoMirrored.Filled.Help, Color(0xFFF3E5F5), Color(0xFF7B1FA2), "Trợ giúp")
        }
    }
}

@Composable
private fun MenuRow(icon: ImageVector, iconBg: Color, iconTint: Color, label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(shape = RoundedCornerShape(12.dp), color = iconBg) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp).size(22.dp),
                    tint = iconTint,
                )
            }
            Text(label, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF212121))
        }
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = Color(0xFFBDBDBD),
        )
    }
}

@Composable
private fun UpdateButtonBar() {
    Button(
        onClick = { },
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1976D2),
            contentColor = Color.White,
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
    ) {
        Text("Cập nhật thông tin", modifier = Modifier.padding(vertical = 6.dp))
    }
}
