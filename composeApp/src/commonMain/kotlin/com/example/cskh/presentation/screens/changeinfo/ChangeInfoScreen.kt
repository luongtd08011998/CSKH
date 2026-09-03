package com.example.cskh.presentation.screens.changeinfo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cskh.domain.model.CustomerProfile
import org.koin.compose.viewmodel.koinViewModel

private val pageBackground = Color(0xFFF5F7FA)
private val headerGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF1565C0), Color(0xFF0288D1)),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeInfoScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ChangeInfoViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    var requestText by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Session expired
    if (state.sessionExpired) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Phiên đăng nhập hết hạn") },
            text = { Text("Vui lòng đăng nhập lại để tiếp tục.") },
            confirmButton = {
                Button(onClick = {
                    viewModel.acknowledgeSessionExpired()
                    onLogout()
                }) { Text("OK") }
            },
        )
    }

    // Success dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {},
            icon = {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(48.dp),
                )
            },
            title = {
                Text(
                    "Yêu cầu đã được gửi",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )
            },
            text = {
                Text(
                    "Nhân viên của chúng tôi sẽ liên hệ và xử lý yêu cầu thay đổi thông tin của bạn trong vòng 24 giờ làm việc.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        requestText = ""
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                ) { Text("Đóng", color = Color.White) }
            },
        )
    }

    // Error dialog
    if (state.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Gửi yêu cầu thất bại", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
            text = { Text(state.errorMessage ?: "", style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearError() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                ) { Text("Đóng", color = Color.White) }
            },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Thay đổi thông tin",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lai",
                            tint = Color.White,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1565C0),
                ),
                windowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
            )
        },
        containerColor = pageBackground,
    ) { padding ->
        when {
            state.isLoading && state.profile == null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Color(0xFF1565C0))
                }
            }

            state.errorMessage != null && state.profile == null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.errorMessage ?: "", color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.refresh() }) { Text("Thử lại") }
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                        .navigationBarsPadding(),
                ) {
                    // Header banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(headerGradient)
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            Surface(
                                modifier = Modifier.size(56.dp),
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f),
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Filled.AccountCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(36.dp),
                                        tint = Color.White,
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = state.profile?.name ?: "—",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                    ),
                                )
                                Text(
                                    text = "Mã KH: ${state.profile?.digiCode ?: "—"}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White.copy(alpha = 0.85f),
                                    ),
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp, bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        // Thông tin hiện tại
                        state.profile?.let { profile ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + slideInVertically { -20 },
                            ) {
                                CurrentInfoCard(profile = profile)
                            }
                        }

                        // Ô nhập yêu cầu
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically { 20 },
                        ) {
                            RequestInputCard(
                                text = requestText,
                                onTextChange = { requestText = it },
                                isSubmitting = state.isSubmitting,
                                onSubmit = {
                                    if (requestText.isNotBlank()) {
                                        viewModel.submitRequest(
                                            currentProfile = state.profile,
                                            requestContent = requestText,
                                            onSuccess = { showSuccessDialog = true },
                                        )
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrentInfoCard(profile: CustomerProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Thông tin hiện tại",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF1F2937),
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE0F2FE),
                ) {
                    Text(
                        "Chỉ đọc",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = Color(0xFF0284C7),
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            InfoRow(
                icon = Icons.Filled.Person,
                iconBg = Color(0xFFDBEAFE),
                iconTint = Color(0xFF2563EB),
                label = "Họ và tên",
                value = profile.name,
                showDivider = true,
            )
            InfoRow(
                icon = Icons.Filled.Tag,
                iconBg = Color(0xFFE8EAF6),
                iconTint = Color(0xFF3949AB),
                label = "Mã khách hàng",
                value = profile.digiCode,
                showDivider = true,
            )
            InfoRow(
                icon = Icons.Filled.Phone,
                iconBg = Color(0xFFD1FAE5),
                iconTint = Color(0xFF059669),
                label = "Số điện thoại",
                value = profile.phone.ifBlank { "Chưa cập nhật" },
                showDivider = true,
            )
            InfoRow(
                icon = Icons.Filled.Email,
                iconBg = Color(0xFFF3E8FF),
                iconTint = Color(0xFF9333EA),
                label = "Email",
                value = profile.email.ifBlank { "Chưa cập nhật" },
                showDivider = true,
            )
            InfoRow(
                icon = Icons.Filled.LocationOn,
                iconBg = Color(0xFFFFE4E6),
                iconTint = Color(0xFFE11D48),
                label = "Địa chỉ",
                value = profile.address.ifBlank { "Chưa cập nhật" },
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
    showDivider: Boolean,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(shape = RoundedCornerShape(10.dp), color = iconBg) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp).size(20.dp),
                    tint = iconTint,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9CA3AF),
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = Color(0xFF1F2937),
                )
            }
        }
        if (showDivider) {
            HorizontalDivider(color = Color(0xFFF3F4F6))
        }
    }
}

@Composable
private fun RequestInputCard(
    text: String,
    onTextChange: (String) -> Unit,
    isSubmitting: Boolean,
    onSubmit: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFFF3E0),
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp).size(20.dp),
                        tint = Color(0xFFEF6C00),
                    )
                }
                Column {
                    Text(
                        "Nội dung yêu cầu thay đổi",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF1F2937),
                    )
                    Text(
                        "Mô tả thông tin bạn muốn cập nhật",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9CA3AF),
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                placeholder = {
                    Text(
                        "Ví dụ: Tôi muốn thay đổi số điện thoại từ 0901... sang 0912...\nHoặc cập nhật địa chỉ mới...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFD1D5DB),
                    )
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1565C0),
                    unfocusedBorderColor = Color(0xFFE5E7EB),
                    focusedContainerColor = Color(0xFFF8FAFF),
                    unfocusedContainerColor = Color(0xFFF9FAFB),
                ),
                maxLines = 7,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "${text.length} ký tự",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF9CA3AF),
                modifier = Modifier.align(Alignment.End),
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = text.isNotBlank() && !isSubmitting,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1565C0),
                    disabledContainerColor = Color(0xFFBFDBFE),
                ),
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        "Gửi yêu cầu thay đổi",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                        ),
                        color = Color.White,
                    )
                }
            }
        }
    }
}
