package com.example.cskh.presentation.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import com.example.cskh.domain.model.MaintenanceArticle
import com.example.cskh.domain.model.NotificationItem
import com.example.cskh.platform.HtmlContentView
import org.koin.compose.viewmodel.koinViewModel

enum class NotificationType {
    BILLING,      // Hóa đơn
    MAINTENANCE,  // Cúp nước/Bảo trì
    GENERAL,      // Tin nổi bật
    FEEDBACK,     // Phản ánh dịch vụ
}

fun String.toNotificationType(): NotificationType {
    return when (this.uppercase()) {
        "BILLING", "INVOICE", "PAYMENT" -> NotificationType.BILLING
        "MAINTENANCE", "WATER_CUT" -> NotificationType.MAINTENANCE
        "FEEDBACK" -> NotificationType.FEEDBACK
        else -> NotificationType.GENERAL
    }
}

fun NotificationType.getIcon(): ImageVector {
    return when (this) {
        NotificationType.BILLING -> Icons.Default.Receipt
        NotificationType.MAINTENANCE -> Icons.Default.WaterDrop
        NotificationType.FEEDBACK -> Icons.Default.Feedback
        NotificationType.GENERAL -> Icons.Default.Info
    }
}

fun NotificationType.getColor(): Color {
    return when (this) {
        NotificationType.BILLING -> Color(0xFF2196F3)       // Xanh dương
        NotificationType.MAINTENANCE -> Color(0xFFEF5350)   // Đỏ
        NotificationType.FEEDBACK -> Color(0xFF4CAF50)      // Xanh lá
        NotificationType.GENERAL -> Color(0xFF9C27B0)       // Tím
    }
}

fun NotificationType.getBackgroundColor(): Color {
    return when (this) {
        NotificationType.BILLING -> Color(0xFFE3F2FD)
        NotificationType.MAINTENANCE -> Color(0xFFFFF3E0)
        NotificationType.FEEDBACK -> Color(0xFFE8F5E9)
        NotificationType.GENERAL -> Color(0xFFF3E5F5)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationListScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onNavigateArticle: (title: String, content: String) -> Unit = { _, _ -> },
    // Spec phananh_reply.md §4: tap vào thông báo FEEDBACK → navigate FeedbackDetailScreen
    onNavigateFeedback: (feedbackId: Long) -> Unit = {},
    // Deep link đến màn hình Hóa đơn khi click vào thông báo Hóa đơn/Thanh toán
    onNavigateInvoices: () -> Unit = {},
    viewModel: NotificationListViewModel = koinViewModel(),
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
    val maintenanceState by viewModel.maintenanceState.collectAsState()
    val featuredState by viewModel.featuredState.collectAsState()
    val uriHandler = LocalUriHandler.current
    val openUrl: (String) -> Unit = remember {
        { url -> uriHandler.openUri(url) }
    }

    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val tabs = listOf(
        "Hóa đơn" to NotificationType.BILLING,
        "Cúp nước" to NotificationType.MAINTENANCE,
        "Nổi bật" to NotificationType.GENERAL
    )

    val notifications = state.items
    val filteredNotifications = when (selectedTab) {
        0 -> notifications.filter { it.type.toNotificationType() == NotificationType.BILLING }
        1 -> emptyList() // Tab Cúp nước dùng API riêng
        2 -> emptyList() // Tab Nổi bật dùng API riêng
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
                        when (selectedTab) {
                            1 -> viewModel.refreshMaintenance()
                            2 -> viewModel.refreshFeatured()
                            else -> viewModel.refresh()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Tải lại",
                            tint = Color.White,
                        )
                    }
                    OutlinedButton(
                        onClick = { viewModel.markAllRead() },
                        enabled = unreadCount > 0 && !state.isMarkingRead,
                        border = BorderStroke(1.dp, if (unreadCount > 0) Color.White.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.3f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White,
                            disabledContentColor = Color.White.copy(alpha = 0.5f)
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(50)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp).padding(end = 4.dp)
                        )
                        Text(
                            text = "Đã đọc hết",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            Surface(
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.White,
                        indicator = { tabPositions ->
                            if (selectedTab < tabPositions.size) {
                                TabRowDefaults.Indicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = Color(0xFFE53935)
                                )
                            }
                        }
                    ) {
                        tabs.forEachIndexed { index, (title, type) ->
                            val isSelected = selectedTab == index
                            val tabCount = notifications.count { it.type.toNotificationType() == type && !it.isRead }
                            Tab(
                                selected = isSelected,
                                onClick = { selectedTab = index },
                                text = {
                                    BadgedBox(
                                        badge = {
                                            if (tabCount > 0) {
                                                Badge(
                                                    containerColor = Color.Red,
                                                    contentColor = Color.White
                                                ) {
                                                    Text(text = "$tabCount")
                                                }
                                            }
                                        }
                                    ) {
                                        Text(
                                            text = title,
                                            color = if (isSelected) Color(0xFF1976D2) else Color.DarkGray,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Tìm kiếm", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Black) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(50),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color(0xFFE0E0E0),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        ),
                        singleLine = true
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    1 -> MaintenanceTabContent(
                        maintenanceState = maintenanceState,
                        searchQuery = searchQuery,
                        onLoadMore = { viewModel.loadMoreMaintenance() },
                        onRetry = { viewModel.refreshMaintenance() },
                        onArticleClick = openUrl,
                    )
                    2 -> FeaturedTabContent(
                        featuredState = featuredState,
                        searchQuery = searchQuery,
                        onLoadMore = { viewModel.loadMoreFeatured() },
                        onRetry = { viewModel.refreshFeatured() },
                        onArticleClick = openUrl,
                    )
                    else -> {
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
                                val finalFilteredNotifications = if (searchQuery.isBlank()) filteredNotifications else {
                                    filteredNotifications.filter { it.title.contains(searchQuery, ignoreCase = true) }
                                }
                                
                                if (finalFilteredNotifications.isEmpty()) {
                                    item {
                                        EmptyView(state.errorMessage ?: "Không có thông báo")
                                    }
                                } else {
                                items(finalFilteredNotifications, key = { it.id }) { notification ->
                                        NotificationCard(
                                            notification = notification,
                                            onClick = {
                                                // Spec phananh_reply.md §4 & §5: mark-as-read sau khi tap
                                                viewModel.markRead(notification.id)
                                                when {
                                                    notification.type.toNotificationType() == NotificationType.FEEDBACK
                                                        && notification.referenceId != null -> {
                                                        onNavigateFeedback(notification.referenceId)
                                                    }
                                                    notification.type.toNotificationType() == NotificationType.BILLING -> {
                                                        onNavigateInvoices()
                                                    }
                                                    notification.referenceId != null -> {
                                                        onNavigateArticle(notification.title, notification.content)
                                                    }
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
        }
    }
}

@Composable
private fun MaintenanceTabContent(
    maintenanceState: MaintenanceUiState,
    searchQuery: String,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    onArticleClick: (String) -> Unit,
) {
    val allArticles = maintenanceState.items
    val articles = if (searchQuery.isBlank()) allArticles else {
        allArticles.filter { it.title.contains(searchQuery, ignoreCase = true) }
    }
    val meta = maintenanceState.meta

    Column(modifier = Modifier.fillMaxSize()) {
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
                            onClick = { onArticleClick("https://beta.toctienltd.vn/news/${article.slug}") }
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
private fun FeaturedTabContent(
    featuredState: FeaturedUiState,
    searchQuery: String,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    onArticleClick: (String) -> Unit,
) {
    val allArticles = featuredState.items
    val articles = if (searchQuery.isBlank()) allArticles else {
        allArticles.filter { it.title.contains(searchQuery, ignoreCase = true) }
    }
    val meta = featuredState.meta

    Column(modifier = Modifier.fillMaxSize()) {
        if (featuredState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (featuredState.errorMessage != null && articles.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = featuredState.errorMessage,
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
                        EmptyView("Không có bài viết nổi bật")
                    }
                } else {
                    items(articles, key = { it.id }) { article ->
                        MaintenanceCard(
                            article = article,
                            onClick = { onArticleClick("https://beta.toctienltd.vn/news/${article.slug}") }
                        )
                    }

                    if (meta != null && featuredState.currentPage < meta.pages - 1 && !featuredState.isLoadingMore) {
                        item {
                            OutlinedButton(
                                onClick = onLoadMore,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "Tải thêm")
                            }
                        }
                    }

                    if (featuredState.isLoadingMore) {
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

private fun stripHtmlTags(html: String): String {
    var text = html.replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
    text = text.replace(Regex("</p>", RegexOption.IGNORE_CASE), "\n\n")
    text = text.replace(Regex("</div>", RegexOption.IGNORE_CASE), "\n")
    text = text.replace(Regex("<li>", RegexOption.IGNORE_CASE), "\n• ")
    
    text = text.replace(Regex("<[^>]*>"), "")
    
    text = text.replace("&nbsp;", " ")
        .replace("&amp;", "&")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
        
    text = text.replace(Regex("[ \\t]+"), " ")
    
    return text.split('\n')
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .joinToString("\n")
}

internal fun formatNotificationDate(createdAt: String): String {
    return try {
        val dateTimeString = createdAt.replace("T", " ")
        val parts = dateTimeString.split(" ")
        if (parts.size >= 2) {
            val dateParts = parts[0].split("-")
            val timeParts = parts[1].split(":")
            if (dateParts.size == 3 && timeParts.size >= 2) {
                "${dateParts[2]}/${dateParts[1]}/${dateParts[0]} • ${timeParts[0]}:${timeParts[1]}"
            } else {
                createdAt
            }
        } else {
            createdAt
        }
    } catch (_: Exception) {
        createdAt
    }
}

@Composable
fun ContactFooter() {
    Text(
        text = buildAnnotatedString {
            append("Phone: ")
            withStyle(style = SpanStyle(color = Color(0xFF2563EB), textDecoration = TextDecoration.Underline)) {
                append("02543894894")
            }
            append(" | Email: ")
            withStyle(style = SpanStyle(color = Color(0xFF2563EB), textDecoration = TextDecoration.Underline)) {
                append("office@toctienltd.vn")
            }
        },
        fontSize = 13.sp,
        color = Color(0xFF424242),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun MaintenanceCard(
    article: MaintenanceArticle,
    onClick: () -> Unit,
) {
    val plainContent = remember(article.content) { stripHtmlTags(article.content) }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = Color(0xFF64B5F6),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = formatNotificationDate(article.createdAt),
                    fontSize = 13.sp,
                    color = Color(0xFF64B5F6)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = article.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = plainContent,
                fontSize = 15.sp,
                color = Color(0xFF212121),
                lineHeight = 22.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            ContactFooter()

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Xem thêm",
                    fontSize = 14.sp
                )
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
    val plainContent = remember(notification.content) { stripHtmlTags(notification.content) }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (!notification.isRead) 3.dp else 2.dp
        ),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = Color(0xFF64B5F6),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = formatNotificationDate(notification.createdAt),
                    fontSize = 13.sp,
                    color = Color(0xFF64B5F6)
                )
                if (!notification.isRead) {
                    Box(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2563EB))
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = notification.title.ifBlank { "Thông báo" },
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = plainContent,
                fontSize = 15.sp,
                color = Color(0xFF212121),
                lineHeight = 22.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            ContactFooter()

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (!notification.url.isNullOrBlank()) {
                        onViewDetail()
                    } else {
                        onClick()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Xem thêm",
                    fontSize = 14.sp
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
