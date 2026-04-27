

## 📋 File cần tạo

### 1️⃣ NotificationModel.kt
**Đường dẫn:** `app/src/main/java/com/company/waterapp/NotificationModel.kt`

```kotlin
package com.company.waterapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class NotificationType {
    BILLING,      // Hóa đơn
    MAINTENANCE,  // Cúp nước/Bảo trì
    GENERAL       // Tin nổi bật
}

data class NotificationModel(
    val id: String,
    val title: String,
    val content: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val type: NotificationType
)

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
```

---

### 2️⃣ NotificationScreen.kt
**Đường dẫn:** `app/src/main/java/com/company/waterapp/NotificationScreen.kt`

```kotlin
package com.company.waterapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// MOCK DATA - Dữ liệu mẫu để hiển thị
val mockNotifications = listOf(
    NotificationModel(
        id = "1",
        title = "Thông báo cúp nước khẩn cấp",
        content = "Khu vực Tóc Tiên sẽ cúp nước từ 8h-12h ngày 26/04 để sửa chữa đường ống chính.",
        timestamp = System.currentTimeMillis() - 1000 * 60 * 30,
        isRead = false,
        type = NotificationType.MAINTENANCE
    ),
    NotificationModel(
        id = "2",
        title = "Hóa đơn tháng 4/2026 đã sẵn sàng",
        content = "Tổng tiền: 145.000đ. Hạn thanh toán: 30/04/2026.",
        timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 2,
        isRead = false,
        type = NotificationType.BILLING
    ),
    NotificationModel(
        id = "3",
        title = "Chương trình khuyến mãi đặc biệt",
        content = "Giảm 10% khi thanh toán online trong tháng 4. Áp dụng cho hóa đơn từ 100.000đ.",
        timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 5,
        isRead = false,
        type = NotificationType.GENERAL
    ),
    NotificationModel(
        id = "4",
        title = "Bảo trì hệ thống lọc nước",
        content = "Áp lực nước có thể giảm từ 14h-18h ngày 27/04.",
        timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24,
        isRead = false,
        type = NotificationType.MAINTENANCE
    ),
    NotificationModel(
        id = "5",
        title = "Nhắc nhở thanh toán hóa đơn",
        content = "Bạn còn 2 ngày để thanh toán hóa đơn tháng 3. Số tiền: 138.000đ.",
        timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 2,
        isRead = true,
        type = NotificationType.BILLING
    ),
    NotificationModel(
        id = "6",
        title = "Nâng cấp dịch vụ khách hàng",
        content = "Ứng dụng đã được cập nhật với nhiều tính năng mới. Cảm ơn quý khách!",
        timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 3,
        isRead = true,
        type = NotificationType.GENERAL
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    var notifications by remember { mutableStateOf(mockNotifications) }
    
    val tabs = listOf(
        "Hóa đơn" to NotificationType.BILLING,
        "Cúp nước" to NotificationType.MAINTENANCE,
        "Nổi bật" to NotificationType.GENERAL
    )
    
    val filteredNotifications = when (selectedTab) {
        0 -> notifications.filter { it.type == NotificationType.BILLING }
        1 -> notifications.filter { it.type == NotificationType.MAINTENANCE }
        2 -> notifications.filter { it.type == NotificationType.GENERAL }
        else -> notifications
    }
    
    val unreadCount = notifications.count { !it.isRead }
    
    Scaffold(
        topBar = {
            TopAppBar(
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
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
                        val tabCount = notifications.count { it.type == type && !it.isRead }
                        
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
                                if (tabCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 2.dp)
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) Color.White.copy(alpha = 0.3f)
                                                else Color.Red
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$tabCount",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // Danh sách thông báo
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (filteredNotifications.isEmpty()) {
                    item {
                        EmptyView()
                    }
                } else {
                    items(filteredNotifications) { notification ->
                        NotificationCard(
                            notification = notification,
                            onClick = {
                                // Đánh dấu đã đọc
                                notifications = notifications.map {
                                    if (it.id == notification.id) it.copy(isRead = true) else it
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notification: NotificationModel,
    onClick: () -> Unit
) {
    val isMaintenance = notification.type == NotificationType.MAINTENANCE
    
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
                notification.type.getBackgroundColor()
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
                    .background(notification.type.getColor()),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = notification.type.getIcon(),
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
                    text = notification.title,
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
                        text = formatTime(notification.timestamp),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
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
fun EmptyView() {
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
            text = "Không có thông báo",
            fontSize = 16.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
    }
}

fun formatTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val minutes = diff / 60000
    val hours = diff / 3600000
    val days = diff / 86400000
    
    return when {
        minutes < 60 -> "$minutes phút trước"
        hours < 24 -> "$hours giờ trước"
        days < 7 -> "$days ngày trước"
        else -> "${days / 7} tuần trước"
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNotificationScreen() {
    NotificationScreen()
}
```

---

## 🚀 Cách sử dụng trong MainActivity

```kotlin
package com.company.waterapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                NotificationScreen()
            }
        }
    }
}
```

---

## 📦 Dependencies cần thiết (build.gradle.kts)

```kotlin
dependencies {
    // Compose
    implementation("androidx.compose.ui:ui:1.6.0")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.compose.material:material-icons-extended:1.6.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.0")
}
```

---

## ✅ Tính năng có sẵn

- ✅ 3 Tab: Hóa đơn | Cúp nước | Nổi bật
- ✅ Icon + màu sắc khác nhau cho từng loại
- ✅ Border đỏ + nền cam nhạt cho thông báo CÚP NƯỚC (nổi bật)
- ✅ Đếm số thông báo chưa đọc
- ✅ Chấm xanh cho thông báo chưa đọc
- ✅ Click vào thông báo → Đánh dấu đã đọc
- ✅ Hiển thị thời gian (30 phút trước, 2 giờ trước...)
- ✅ Dữ liệu mẫu (mock data) để test ngay

---

## 🎨 Tùy chỉnh màu sắc

**Thay đổi trong `NotificationModel.kt`:**

```kotlin
// Màu Icon + Button
NotificationType.BILLING -> Color(0xFF2196F3)      // Xanh dương
NotificationType.MAINTENANCE -> Color(0xFFEF5350)  // Đỏ
NotificationType.GENERAL -> Color(0xFF9C27B0)      // Tím

// Màu nền Card
NotificationType.BILLING -> Color(0xFFE3F2FD)      // Xanh nhạt
NotificationType.MAINTENANCE -> Color(0xFFFFF3E0)  // Cam nhạt
NotificationType.GENERAL -> Color(0xFFF3E5F5)      // Tím nhạt
```

---

**Copy & Paste là chạy! 🚀**
