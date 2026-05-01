package com.example.cskh.presentation.screens.phananh

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cskh.platform.PickedImage
import com.example.cskh.platform.rememberImagePicker
import org.koin.compose.viewmodel.koinViewModel
import kotlin.random.Random

private val pageBg = Brush.verticalGradient(listOf(Color(0xFFEFF6FF), Color(0xFFE0F2FE)))

private data class IssueType(
    val value: String,
    val label: String,
    val emoji: String,
)

private val issueTypes = listOf(
    IssueType("leak", "Rò rỉ nước", "💧"),
    IssueType("quality", "Chất lượng nước", "🚰"),
    IssueType("pressure", "Áp lực nước yếu", "📉"),
    IssueType("outage", "Mất nước", "🚫"),
    IssueType("billing", "Hóa đơn", "💵"),
    IssueType("meter", "Đồng hồ nước", "⏱️"),
    IssueType("other", "Khác", "📝"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhanAnhScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onNavigateDetail: (Long) -> Unit = {},
    viewModel: PhanAnhViewModel = koinViewModel(),
) {
    val vmState by viewModel.state.collectAsState()

    if (vmState.sessionExpired) {
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
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var selectedIssue by remember { mutableStateOf<String?>(null) }
    var address by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var photos by remember { mutableStateOf<List<PickedImage>>(emptyList()) }
    var showPhotoSheet by remember { mutableStateOf(false) }

    var submittedCode by remember { mutableStateOf<String?>(null) }

    val imagePicker = rememberImagePicker(
        onResult = { picked: List<PickedImage> ->
            val merged = (photos + picked).distinctBy { it.uri }.take(5)
            photos = merged
        },
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Phản ánh dịch vụ") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
            )
        },
        containerColor = Color.Transparent,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // Tab bar
            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = Color(0xFF2563EB),
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Text(
                            "Gửi phản ánh",
                            fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal,
                        )
                    },
                    selectedContentColor = Color(0xFF2563EB),
                    unselectedContentColor = Color(0xFF64748B),
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Text(
                            "Lịch sử",
                            fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal,
                        )
                    },
                    selectedContentColor = Color(0xFF2563EB),
                    unselectedContentColor = Color(0xFF64748B),
                )
            }

            // Tab content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(pageBg),
            ) {
                if (selectedTabIndex == 0) {
                    val tracking = vmState.submittedTrackingCode ?: submittedCode
                    if (tracking != null) {
                        SuccessState(
                            code = tracking,
                            onNew = {
                                selectedIssue = null
                                address = ""
                                description = ""
                                photos = emptyList()
                                submittedCode = null
                                viewModel.resetSubmitted()
                            },
                        )
                    } else {
                        FormState(
                            selectedIssue = selectedIssue,
                            onSelectIssue = { selectedIssue = it },
                            address = address,
                            onAddressChange = { address = it },
                            description = description,
                            onDescriptionChange = { description = it },
                            photos = photos,
                            onPickPhotos = {
                                showPhotoSheet = true
                            },
                            onRemovePhoto = { uri ->
                                photos = photos.filterNot { it.uri == uri }
                            },
                            isSubmitting = vmState.isSubmitting,
                            errorMessage = vmState.errorMessage,
                            onDismissError = { viewModel.clearError() },
                            onSubmit = {
                                val issue = selectedIssue ?: return@FormState
                                viewModel.submit(
                                    issueType = issue,
                                    location = address,
                                    description = description,
                                    images = photos,
                                )
                            },
                        )
                    }
                } else {
                    FeedbackHistoryTab(onItemClick = onNavigateDetail)
                }

                if (showPhotoSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showPhotoSheet = false },
                        containerColor = Color.White,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Text(
                                text = "Hình ảnh minh họa",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = Color(0xFF0F172A),
                            )
                            Spacer(Modifier.height(4.dp))
                            SheetAction(
                                title = "Chọn ảnh",
                                subtitle = "Tối đa 5 ảnh",
                                onClick = {
                                    showPhotoSheet = false
                                    imagePicker.pickImages(max = 5)
                                },
                            )
                            SheetAction(
                                title = "Chụp ảnh",
                                subtitle = "Mở camera",
                                onClick = {
                                    showPhotoSheet = false
                                    imagePicker.takePhoto()
                                },
                            )
                            SheetAction(
                                title = "Hủy",
                                subtitle = "",
                                onClick = { showPhotoSheet = false },
                                isDestructive = true,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FormState(
    selectedIssue: String?,
    onSelectIssue: (String) -> Unit,
    address: String,
    onAddressChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    photos: List<PickedImage>,
    onPickPhotos: () -> Unit,
    onRemovePhoto: (String) -> Unit, // uri
    isSubmitting: Boolean,
    errorMessage: String?,
    onDismissError: () -> Unit,
    onSubmit: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Phản ánh dịch vụ",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF0F172A),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Gửi thông tin để chúng tôi hỗ trợ bạn",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF475569),
                )
            }
        }

        CardBlock(title = "Loại vấn đề *") {
            val rows = issueTypes.chunked(2)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                rows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        row.forEach { item ->
                            IssueTypeTile(
                                modifier = Modifier.weight(1f),
                                item = item,
                                selected = selectedIssue == item.value,
                                onClick = { onSelectIssue(item.value) },
                            )
                        }
                        repeat(2 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
                    }
                }
            }
        }

        CardBlock(title = "Địa điểm *") {
            OutlinedTextField(
                value = address,
                onValueChange = onAddressChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Nhập địa chỉ cụ thể") },
                singleLine = true,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "VD: Số nhà, tên đường, phường/xã",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B),
            )
        }

        CardBlock(title = "Mô tả chi tiết *") {
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                placeholder = { Text("Mô tả tình trạng, thời gian xảy ra, ảnh hưởng...") },
            )
        }

        CardBlock(title = "Hình ảnh minh họa (Tùy chọn)") {
            PhotoUploadCard(
                onClick = onPickPhotos,
                subtitle = "Tối đa 5 ảnh",
            )
            if (photos.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    photos.forEach { img ->
                        PhotoChip(
                            name = img.name,
                            onRemove = { onRemovePhoto(img.uri) },
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
            border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    imageVector = Icons.Filled.WarningAmber,
                    contentDescription = null,
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(20.dp),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Lưu ý:",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Color(0xFF1E3A8A),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "• Phản ánh khẩn cấp sẽ được xử lý trong vòng 4h\n" +
                            "• Bạn sẽ nhận mã số theo dõi sau khi gửi\n" +
                            "• Thông tin liên hệ lấy từ hồ sơ đã đăng ký",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF1D4ED8),
                    )
                }
            }
        }

        errorMessage?.let { msg ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                border = BorderStroke(1.dp, Color(0xFFEF9A9A)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = msg,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFC62828),
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(Modifier.size(10.dp))
                    Text(
                        text = "Đóng",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = Color(0xFFC62828),
                        modifier = Modifier.clickable(onClick = onDismissError),
                    )
                }
            }
        }

        val canSubmit = selectedIssue != null && address.isNotBlank() && description.isNotBlank()
        Button(
            onClick = onSubmit,
            enabled = canSubmit && !isSubmitting,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
        ) {
            Icon(Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(10.dp))
            Text(
                if (isSubmitting) "Đang gửi..." else "Gửi phản ánh",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
            )
        }
        Spacer(Modifier.height(6.dp))
    }
}

@Composable
private fun PhotoChip(
    name: String,
    onRemove: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFEFF6FF),
        border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
    ) {
        Row(
            modifier = Modifier.padding(start = 10.dp, end = 6.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = "📷 $name",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF1D4ED8),
            )
            Surface(
                shape = CircleShape,
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                modifier = Modifier
                    .size(22.dp)
                    .clickable(onClick = onRemove),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Xóa ảnh",
                        tint = Color(0xFF1D4ED8),
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoUploadCard(
    onClick: () -> Unit,
    subtitle: String,
    height: Dp = 140.dp,
) {
    val dash = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
    val borderColor = Color(0xFFD1D5DB)
    val shape = RoundedCornerShape(18.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(Color(0xFFF8FAFC))
            .dashedBorder(2.dp, borderColor, shape, dash)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.PhotoCamera,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(34.dp),
            )
            Text(
                text = "Chụp hoặc chọn ảnh",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color(0xFF334155),
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B),
            )
        }
    }
}

@Composable
private fun SheetAction(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false,
) {
    val titleColor = if (isDestructive) MaterialTheme.colorScheme.error else Color(0xFF0F172A)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = titleColor,
            )
            if (subtitle.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B),
                )
            }
        }
    }
}

private fun Modifier.dashedBorder(
    strokeWidth: Dp,
    color: Color,
    shape: RoundedCornerShape,
    pathEffect: PathEffect,
): Modifier {
    return this.then(
        Modifier.background(Color.Transparent).drawBehind {
            val stroke = Stroke(width = strokeWidth.toPx(), pathEffect = pathEffect)
            drawRoundRect(
                color = color,
                style = stroke,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                    x = shape.topStart.toPx(size, this),
                    y = shape.topStart.toPx(size, this),
                ),
            )
        },
    )
}

@Composable
private fun SuccessState(
    code: String,
    onNew: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Surface(
                    modifier = Modifier.size(84.dp),
                    shape = CircleShape,
                    color = Color(0xFFE8F5E9),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(48.dp),
                        )
                    }
                }
                Text(
                    text = "Gửi thành công!",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF0F172A),
                )
                Text(
                    text = "Phản ánh của bạn đã được tiếp nhận",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF475569),
                )
                Text(
                    text = "Mã số: #$code",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B),
                )
                Text(
                    text = "Chúng tôi sẽ liên hệ trong vòng 24h",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B),
                )
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = onNew,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                ) {
                    Text("Gửi phản ánh mới", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun CardBlock(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = Color(0xFF334155),
            )
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun IssueTypeTile(
    modifier: Modifier,
    item: IssueType,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (selected) Color(0xFF2563EB) else Color(0xFFE2E8F0)
    val bg = if (selected) Color(0xFFEFF6FF) else Color.White
    val elevation = if (selected) 4.dp else 0.dp

    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        border = BorderStroke(2.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(item.emoji, style = MaterialTheme.typography.headlineSmall)
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = Color(0xFF0F172A),
            )
        }
    }
}

