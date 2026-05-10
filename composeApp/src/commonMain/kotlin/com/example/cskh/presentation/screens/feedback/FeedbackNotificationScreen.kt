package com.example.cskh.presentation.screens.feedback

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cskh.presentation.screens.notifications.NotificationCard
import com.example.cskh.presentation.screens.notifications.EmptyView
import com.example.cskh.presentation.screens.notifications.formatNotificationDate
import com.example.cskh.domain.model.NotificationItem
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackNotificationScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onNavigateFeedback: (feedbackId: Long) -> Unit = {},
    onNavigateFeedbackList: () -> Unit = {},
    viewModel: FeedbackNotificationViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    if (state.sessionExpired) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Phiên đăng nhập hết hạn") },
            text = { Text("Phiên làm việc của bạn đã hết hạn. Vui lòng đăng nhập lại để tiếp tục sử dụng.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.acknowledgeSessionExpired()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF6C00)),
                ) {
                    Text("OK", color = Color.White)
                }
            },
        )
    }

    val unreadCount = state.items.count { !it.isRead }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White,
                        )
                    }
                },
                title = {
                    Column {
                        Text(
                            text = "Thông báo Phản ánh",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                        )
                        Text(
                            text = "Cập nhật từ nhân viên hỗ trợ",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Tải lại",
                            tint = Color.White,
                        )
                    }
                    if (unreadCount > 0) {
                        OutlinedButton(
                            onClick = { viewModel.markAllRead() },
                            enabled = !state.isMarkingRead,
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.7f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            modifier = Modifier.height(32.dp),
                            shape = RoundedCornerShape(50),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(end = 4.dp),
                            )
                            Text("Đã đọc hết", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFEF6C00),   // Màu cam - nhất quán với icon Phản ánh
                    titleContentColor = Color.White,
                ),
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5)),
        ) {
            // Banner dẫn đến Danh sách Phản ánh
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFFF3E0),
                tonalElevation = 2.dp,
                onClick = onNavigateFeedbackList,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Feedback,
                        contentDescription = null,
                        tint = Color(0xFFEF6C00),
                        modifier = Modifier.size(24.dp),
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Quản lý Phản ánh",
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFE65100),
                            fontSize = 14.sp,
                        )
                        Text(
                            text = "Xem tất cả phiếu phản ánh, tạo phiếu mới",
                            color = Color(0xFF795548),
                            fontSize = 12.sp,
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFFEF6C00),
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFFEF6C00))
                    }
                } else {
                    val items = state.items
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        if (items.isEmpty()) {
                            item {
                                EmptyView(state.errorMessage ?: "Chưa có thông báo phản ánh nào")
                            }
                        } else {
                            items(items, key = { it.id }) { notification ->
                                NotificationCard(
                                    notification = notification,
                                    onClick = {
                                        viewModel.markItemRead(notification.id)
                                        notification.referenceId?.let { onNavigateFeedback(it) }
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
