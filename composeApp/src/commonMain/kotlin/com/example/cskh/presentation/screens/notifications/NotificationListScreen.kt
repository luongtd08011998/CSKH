package com.example.cskh.presentation.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.cskh.domain.model.MaintenanceArticle
import com.example.cskh.domain.model.NotificationItem
import org.koin.compose.viewmodel.koinViewModel

enum class NotificationType {
    BILLING,      // Hóa đơn
    MAINTENANCE,  // Cúp nước/Bảo trì
    GENERAL       // Tin nổi bật
}

fun String.toNotificationType(): NotificationType {
    return when (this.uppercase()) {
        "BILLING", "INVOICE", "PAYMENT" -> NotificationType.BILLING
        "MAINTENANCE", "WATER_CUT" -> NotificationType.MAINTENANCE
        else -> NotificationType.GENERAL
    }
}

fun NotificationType.getIcon(): ImageVector {
    return when (this) {
        NotificationType.BILLING -> Icons.Default.Receipt
        NotificationType.MAINTENANCE -> Icons.Default.WaterDrop
        NotificationType.GENERAL -> Icons.Default.Info
    }
}

fun NotificationType.getColor(): Color {
    return when (this) {
        NotificationType.BILLING -> Color(0xFF2196F3)      // Xanh dương
        NotificationType.MAINTENANCE -> Color(0xFFEF5350)  // Đỏ
        NotificationType.GENERAL -> Color(0xFF9C27B0)      // Tím
    }
}

fun NotificationType.getBackgroundColor(): Color {
    return when (this) {
        NotificationType.BILLING -> Color(0xFFE3F2FD)
        NotificationType.MAINTENANCE -> Color(0xFFFFF3E0)  // Cam nhạt
        NotificationType.GENERAL -> Color(0xFFF3E5F5)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationListScreen(
    onBack: () -> Unit,
    onNavigateArticle: (title: String, content: String) -> Unit = { _, _ -> },
    viewModel: NotificationListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val maintenanceState by viewModel.maintenanceState.collectAsState()
    val uriHandler = LocalUriHandler.current
    val openUrl: (String) -> Unit = remember {
        { url -> uriHandler.openUri(url) }
    }

    var selectedTab by remember { mutableStateOf(0) }

    val tabs = listOf(
        "Hóa đơn" to NotificationType.BILLING,
        "Cúp nước" to NotificationType.MAINTENANCE,
        "Nổi bật" to NotificationType.GENERAL
    )

    val notifications = state.items
    val filteredNotifications = when (selectedTab) {
        0 -> notifications.filter { it.type.toNotificationType() == NotificationType.BILLING }
        1 -> emptyList() // Tab Cúp nước dùng API riêng
        2 -> notifications.filter { it.type.toNotificationType() == NotificationType.GENERAL }
        else -> notifications
    }

    val unreadCount = notifications.count { !it.isRead }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White
                        )
                    }
                },
                title = {
                    Column {
                        Text(
                            text = "Thông báo",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Công ty Cấp nước Tóc Tiên",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (selectedTab == 1) viewModel.refreshMaintenance()
                        else viewModel.refresh()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Tải lại",
                            tint = Color.White,
                        )
                    }
                    if (selectedTab != 1) {
                        IconButton(
                            onClick = { viewModel.markAllRead() },
                            enabled = unreadCount > 0 && !state.isMarkingRead,
                        ) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = "Đánh dấu tất cả đã đọc",
                                tint = if (unreadCount > 0) Color.White else Color.White.copy(alpha = 0.5f),
                            )
                        }
                    }
                    Box(modifier = Modifier.padding(end = 12.dp)) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Bell",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        if (unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 4.dp, y = (-4).dp)
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$unreadCount",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            when (selectedTab) {
                1 -> MaintenanceTabContent(
                    maintenanceState = maintenanceState,
                    onLoadMore = { viewModel.loadMoreMaintenance() },
                    onRetry = { viewModel.refreshMaintenance() },
                    onArticleClick = openUrl,
                )
                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Tabs
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(0.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                tabs.forEachIndexed { index, (title, type) ->
                                    val isSelected = selectedTab == index
                                    val tabCount = when (index) {
                                        1 -> 0 // Tab Cúp nước không có unread count
                                        else -> notifications.count { it.type.toNotificationType() == type && !it.isRead }
                                    }

                                    Button(
                                        onClick = { selectedTab = index },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isSelected) type.getColor() else Color(0xFFE0E0E0),
                                            contentColor = if (isSelected) Color.White else Color.Gray
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(vertical = 12.dp)
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            BadgedBox(
                                                badge = {
                                                    if (tabCount > 0) {
                                                        Badge(
                                                            containerColor = if (isSelected) Color.White.copy(alpha = 0.9f) else Color.Red,
                                                            contentColor = if (isSelected) type.getColor() else Color.White
                                                        ) {
                                                            Text(text = "$tabCount")
                                                        }
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = type.getIcon(),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = title,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (state.isLoading) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                if (filteredNotifications.isEmpty()) {
                                    item {
                                        EmptyView(state.errorMessage ?: "Không có thông báo")
                                    }
                                } else {
                                    items(filteredNotifications, key = { it.id }) { notification ->
                                        NotificationCard(
                                            notification = notification,
                                            onClick = {
                                                viewModel.markRead(notification.id)
                                                if (notification.referenceId != null) {
                                                    onNavigateArticle(notification.title, notification.content)
                                                }
                                            },
                                            onViewDetail = {
                                                viewModel.markRead(notification.id)
                                                openUrl(notification.url!!)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Tabs luôn hiển thị ở trên cùng (dùng cho tab Cúp nước)
            if (selectedTab == 1) {
                Card(
                    modifier = Modifier
                        .align(Alignment.TopCenter),
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tabs.forEachIndexed { index, (title, type) ->
                            val isSelected = selectedTab == index
                            Button(
                                onClick = { selectedTab = index },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) type.getColor() else Color(0xFFE0E0E0),
                                    contentColor = if (isSelected) Color.White else Color.Gray
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(vertical = 12.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = type.getIcon(),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = title,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MaintenanceTabContent(
    maintenanceState: MaintenanceUiState,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    onArticleClick: (String) -> Unit,
) {
    val articles = maintenanceState.items
    val meta = maintenanceState.meta

    Column(modifier = Modifier.fillMaxSize().padding(top = 72.dp)) {
        if (maintenanceState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (maintenanceState.errorMessage != null && articles.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = maintenanceState.errorMessage,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onRetry) {
                    Text(text = "Thử lại")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (articles.isEmpty()) {
                    item {
                        EmptyView("Không có bài viết bảo trì")
                    }
                } else {
                    items(articles, key = { it.id }) { article ->
                        MaintenanceCard(
                            article = article,
                            onClick = { onArticleClick("https://capnuoctoctien.vn/news/${article.slug}") }
                        )
                    }

                    // Nút tải thêm
                    if (meta != null && maintenanceState.currentPage < meta.pages - 1 && !maintenanceState.isLoadingMore) {
                        item {
                            OutlinedButton(
                                onClick = onLoadMore,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "Tải thêm")
                            }
                        }
                    }

                    // Loading indicator khi tải thêm
                    if (maintenanceState.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MaintenanceCard(
    article: MaintenanceArticle,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(2.dp, Color(0xFFEF5350), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3E0)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Thumbnail
            if (!article.thumbnail.isNullOrBlank()) {
                AsyncImage(
                    model = article.thumbnail,
                    contentDescription = article.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEF5350)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Nội dung
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = article.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = article.content.take(120) + if (article.content.length > 120) "..." else "",
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        lineHeight = 18.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = article.createdAt,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${article.views}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = onClick,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF1976D2)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Launch,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Xem chi tiết",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notification: NotificationItem,
    onClick: () -> Unit,
    onViewDetail: () -> Unit = {},
) {
    val notificationType = notification.type.toNotificationType()
    val isMaintenance = notificationType == NotificationType.MAINTENANCE

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .then(
                if (isMaintenance) {
                    Modifier.border(2.dp, Color(0xFFEF5350), RoundedCornerShape(16.dp))
                } else Modifier
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isMaintenance) {
                Color(0xFFFFF3E0)  // Cam nhạt cho cúp nước
            } else {
                notificationType.getBackgroundColor()
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (!notification.isRead) 4.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(notificationType.getColor()),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = notificationType.getIcon(),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            // Nội dung
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notification.title.ifBlank { "Thông báo" },
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (notification.isRead) Color.Gray else Color.Black
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = notification.content,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = notification.createdAt,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                if (notification.url != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = onViewDetail,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF1976D2)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Launch,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Xem chi tiết",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Chấm xanh nếu chưa đọc
            if (!notification.isRead) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2196F3))
                )
            }
        }
    }
}

@Composable
fun EmptyView(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.NotificationsNone,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            fontSize = 16.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
    }
}
