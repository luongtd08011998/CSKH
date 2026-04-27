package com.example.cskh.presentation.screens.phananh

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentLate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.cskh.domain.model.FeedbackItem
import org.koin.compose.viewmodel.koinViewModel

private val pageBgList = Brush.verticalGradient(listOf(Color(0xFFEFF6FF), Color(0xFFE0F2FE)))

@Composable
internal fun FeedbackHistoryTab(
    modifier: Modifier = Modifier,
    viewModel: PhanAnhListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = modifier.fillMaxSize().background(pageBgList),
    ) {
        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF2563EB),
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(44.dp),
                        )
                        Text(
                            text = "Đang tải danh sách...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF475569),
                        )
                    }
                }
            }

            state.errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SentimentDissatisfied,
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
                            Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Thử lại", color = Color.White)
                        }
                    }
                }
            }

            state.feedbacks.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text("📭", style = MaterialTheme.typography.displayMedium)
                        Text(
                            text = "Bạn chưa gửi phản ánh nào",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = Color(0xFF334155),
                        )
                        Text(
                            text = "Hãy gửi phản ánh nếu bạn gặp sự cố về dịch vụ nước",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B),
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item { Spacer(Modifier.height(4.dp)) }
                    items(state.feedbacks, key = { it.id }) { feedback ->
                        FeedbackListItem(feedback = feedback)
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }
}

@Composable
private fun FeedbackListItem(feedback: FeedbackItem) {
    val statusConfig = feedbackStatusConfig(feedback.status)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row: issueType + status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFEFF6FF),
                        modifier = Modifier.size(36.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = issueTypeEmoji(feedback.issueType),
                                style = MaterialTheme.typography.titleSmall,
                            )
                        }
                    }
                    Column {
                        Text(
                            text = issueTypeLabel(feedback.issueType),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF0F172A),
                        )
                        Text(
                            text = "#${feedback.trackingCode}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B),
                        )
                    }
                }
                StatusBadge(statusConfig)
            }

            Spacer(Modifier.height(10.dp))

            // Description
            Text(
                text = feedback.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF475569),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(8.dp))

            // Footer: location + date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "📍 ${feedback.location}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = formatFeedbackDate(feedback.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8),
                )
            }

            // Image count chip if any
            if (feedback.images.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF1F5F9),
                ) {
                    Text(
                        text = "🖼 ${feedback.images.size} ảnh đính kèm",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(config: StatusConfig) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = config.bg,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = config.icon,
                contentDescription = null,
                tint = config.iconTint,
                modifier = Modifier.size(12.dp),
            )
            Text(
                text = config.label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = config.textColor,
            )
        }
    }
}

private data class StatusConfig(
    val label: String,
    val bg: Color,
    val textColor: Color,
    val icon: ImageVector,
    val iconTint: Color,
)

private fun feedbackStatusConfig(status: String): StatusConfig = when (status.uppercase()) {
    "PENDING" -> StatusConfig(
        label = "Chờ xử lý",
        bg = Color(0xFFFFF3CD),
        textColor = Color(0xFF92600A),
        icon = Icons.Filled.HourglassEmpty,
        iconTint = Color(0xFFD97706),
    )
    "PROCESSING" -> StatusConfig(
        label = "Đang xử lý",
        bg = Color(0xFFDBEAFE),
        textColor = Color(0xFF1E40AF),
        icon = Icons.Filled.HourglassEmpty,
        iconTint = Color(0xFF2563EB),
    )
    "RESOLVED" -> StatusConfig(
        label = "Đã giải quyết",
        bg = Color(0xFFD1FAE5),
        textColor = Color(0xFF065F46),
        icon = Icons.Filled.CheckCircle,
        iconTint = Color(0xFF059669),
    )
    "REJECTED" -> StatusConfig(
        label = "Từ chối",
        bg = Color(0xFFFFE4E6),
        textColor = Color(0xFF9F1239),
        icon = Icons.Filled.AssignmentLate,
        iconTint = Color(0xFFE11D48),
    )
    else -> StatusConfig(
        label = status,
        bg = Color(0xFFF1F5F9),
        textColor = Color(0xFF475569),
        icon = Icons.Filled.HourglassEmpty,
        iconTint = Color(0xFF94A3B8),
    )
}

private fun issueTypeEmoji(issueType: String): String = when (issueType.uppercase()) {
    "LEAK" -> "💧"
    "QUALITY" -> "🚰"
    "PRESSURE" -> "📉"
    "OUTAGE" -> "🚫"
    "BILLING" -> "💵"
    "METER" -> "⏱️"
    else -> "📝"
}

private fun issueTypeLabel(issueType: String): String = when (issueType.uppercase()) {
    "LEAK" -> "Rò rỉ nước"
    "QUALITY" -> "Chất lượng nước"
    "PRESSURE" -> "Áp lực nước yếu"
    "OUTAGE" -> "Mất nước"
    "BILLING" -> "Hóa đơn"
    "METER" -> "Đồng hồ nước"
    else -> "Khác"
}

private fun formatFeedbackDate(createdAt: String): String {
    // Parse ISO-8601 "2026-04-24T16:10:00.12345" -> "24/04/2026"
    return try {
        val datePart = createdAt.substringBefore('T')
        val parts = datePart.split('-')
        if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else datePart
    } catch (_: Exception) {
        createdAt
    }
}
