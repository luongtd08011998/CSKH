package com.example.cskh.presentation.screens.phananh

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.cskh.domain.model.FeedbackDetail
import com.example.cskh.domain.model.StaffReply
import com.example.cskh.domain.usecase.UserFormPreferencesUseCase
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhanAnhDetailScreen(
    feedbackId: Long,
    onBack: () -> Unit,
    viewModel: PhanAnhDetailViewModel = koinViewModel(parameters = { parametersOf(feedbackId) }),
) {
    val state by viewModel.state.collectAsState()
    val preferences = koinInject<UserFormPreferencesUseCase>()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết phản ánh") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Tải lại")
                    }
                },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF9FAFB)),
        ) {
            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF2563EB))
                    }
                }
                state.errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Icon(
                                Icons.Default.SentimentDissatisfied,
                                contentDescription = null,
                                tint = Color(0xFFCB2D3E),
                                modifier = Modifier.size(56.dp),
                            )
                            Text(
                                text = state.errorMessage ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF64748B),
                            )
                            Button(
                                onClick = { viewModel.refresh() },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Thử lại", color = Color.White)
                            }
                        }
                    }
                }
                state.detail != null -> {
                    val baseUrl = preferences.getBaseUrl().trimEnd('/')
                    val detail = state.detail!!
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        item { Spacer(Modifier.height(8.dp)) }
                        item {
                            InfoCard(detail = detail)
                        }
                        if (detail.images.isNotEmpty()) {
                            item {
                                ImagesSection(
                                    images = detail.images,
                                    baseUrl = baseUrl,
                                )
                            }
                        }
                        item {
                            RepliesSection(
                                replies = detail.replies,
                                baseUrl = baseUrl,
                            )
                        }
                        item { Spacer(Modifier.height(24.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(detail: FeedbackDetail) {
    val statusConfig = feedbackStatusConfig(detail.status)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Status + tracking code row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PulsingDot(color = statusConfig.iconTint)
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = statusConfig.bg,
                    ) {
                        Text(
                            text = statusConfig.label,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                            ),
                            color = statusConfig.textColor,
                        )
                    }
                }
                Text(
                    text = "#${detail.trackingCode}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                    ),
                    color = Color(0xFF6B7280),
                )
            }

            Spacer(Modifier.height(20.dp))

            // Info rows — each with colored background card
            InfoTile(
                icon = Icons.Default.Description,
                iconTint = Color(0xFF2563EB),
                bgColor = Color(0xFFEFF6FF),
                label = "Loại vấn đề",
                value = issueTypeLabel(detail.issueType),
            )
            Spacer(Modifier.height(12.dp))
            InfoTile(
                icon = Icons.Default.LocationOn,
                iconTint = Color(0xFF16A34A),
                bgColor = Color(0xFFF0FDF4),
                label = "Vị trí",
                value = detail.location,
            )
            Spacer(Modifier.height(12.dp))
            InfoTile(
                icon = Icons.Default.CalendarMonth,
                iconTint = Color(0xFF9333EA),
                bgColor = Color(0xFFFAF5FF),
                label = "Ngày gửi",
                value = formatFeedbackDate(detail.createdAt),
            )
            Spacer(Modifier.height(12.dp))

            // Description
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF9FAFB),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Mô tả",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = Color(0xFF6B7280),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = detail.description,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = Color(0xFF111827),
                        lineHeight = 24.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun PulsingDot(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "alpha",
    )
    Surface(
        modifier = Modifier.size(12.dp).alpha(alpha),
        shape = CircleShape,
        color = color,
    ) {}
}

@Composable
private fun InfoTile(
    icon: ImageVector,
    iconTint: Color,
    bgColor: Color,
    label: String,
    value: String,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6B7280),
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = Color(0xFF111827),
                )
            }
        }
    }
}

@Composable
private fun ImagesSection(
    images: List<String>,
    baseUrl: String,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = Color(0xFF374151),
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Hình ảnh đính kèm",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = Color(0xFF111827),
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "${images.size} ảnh",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6B7280),
                )
            }
            Spacer(Modifier.height(12.dp))

            val rows = images.chunked(3)
            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    row.forEach { imgPath ->
                        val fullUrl = if (imgPath.startsWith("http")) imgPath else "$baseUrl$imgPath"
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp)),
                            color = Color(0xFFF3F4F6),
                        ) {
                            AsyncImage(
                                model = fullUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }
                    repeat(3 - row.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
                if (row != rows.last()) Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun RepliesSection(
    replies: List<StaffReply>,
    baseUrl: String,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = null,
                    tint = Color(0xFF374151),
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Phản hồi từ công ty",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = Color(0xFF111827),
                )
            }

            if (replies.isEmpty()) {
                Spacer(Modifier.height(20.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        color = Color(0xFFF3F4F6),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Business,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(32.dp),
                            )
                        }
                    }
                    Text(
                        text = "Chưa có phản hồi",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF6B7280),
                    )
                    Text(
                        text = "Công ty sẽ phản hồi sớm nhất có thể",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9CA3AF),
                    )
                }
            } else {
                Spacer(Modifier.height(12.dp))
                replies.forEach { reply ->
                    ReplyCard(reply = reply, baseUrl = baseUrl)
                    if (reply != replies.last()) {
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ReplyCard(reply: StaffReply, baseUrl: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFEFF6FF),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val avatarUrl = if (reply.staffAvatar.startsWith("http")) reply.staffAvatar else "$baseUrl${reply.staffAvatar}"
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = Color(0xFFDBEAFE),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (avatarUrl.isNotBlank() && avatarUrl != baseUrl) {
                            AsyncImage(
                                model = avatarUrl,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp).clip(CircleShape),
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(22.dp),
                            )
                        }
                    }
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reply.staffName.ifBlank { "Nhân viên" },
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF111827),
                    )
                    Text(
                        text = formatFeedbackDate(reply.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF9CA3AF),
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = reply.content,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF111827),
                lineHeight = 22.sp,
            )
        }
    }
}
