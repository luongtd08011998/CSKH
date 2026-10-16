package com.example.cskh.presentation.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cskh.platform.PdfDocumentView
import org.koin.compose.viewmodel.koinViewModel

private val pageBackground = Color(0xFFF5F7FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDocumentsScreen(
    onBack: () -> Unit,
    viewModel: CustomerDocumentsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Hồ sơ khách hàng",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        ),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White,
                        )
                    }
                },
                actions = {
                    if (state.pdfFilePath != null) {
                        IconButton(onClick = { viewModel.openInExternalViewer() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = "Xem toàn màn hình",
                                tint = Color.White,
                            )
                        }
                    }
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Làm mới",
                            tint = Color.White,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1565C0),
                ),
                windowInsets = WindowInsets(0, 0, 0, 0),
            )
        },
        containerColor = pageBackground,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // Phần 1: Thẻ thông tin khách hàng (Tên, Mã KH, Tên file)
            CustomerSummaryHeader(
                customerName = state.customerName,
                digiCode = state.digiCode,
                fileName = state.selectedDoc?.filename ?: "",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            )

            // Nếu có nhiều hơn 1 tài liệu, hiển thị thanh tab chọn tài liệu
            if (state.documents.size > 1) {
                val selectedIndex = state.documents.indexOf(state.selectedDoc).coerceAtLeast(0)
                ScrollableTabRow(
                    selectedTabIndex = selectedIndex,
                    containerColor = Color.White,
                    contentColor = Color(0xFF1565C0),
                    edgePadding = 14.dp,
                ) {
                    state.documents.forEachIndexed { index, doc ->
                        Tab(
                            selected = state.selectedDoc == doc,
                            onClick = { viewModel.loadAndDisplayPdf(doc) },
                            text = {
                                Text(
                                    text = doc.filename,
                                    maxLines = 1,
                                    fontWeight = if (state.selectedDoc == doc) FontWeight.Bold else FontWeight.Normal,
                                )
                            },
                        )
                    }
                }
            }

            // Phần 2: Bên dưới hiển thị trực tiếp file .pdf
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    // Trạng thái đang tải danh sách hoặc tải file PDF
                    state.isLoading || state.isDownloadingPdf -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(24.dp),
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(40.dp),
                                strokeWidth = 3.dp,
                                color = Color(0xFF1565C0),
                            )
                            Text(
                                text = "Đang tải tệp hồ sơ PDF...",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF334155),
                                ),
                            )
                        }
                    }

                    // Trạng thái lỗi
                    state.errorMessage != null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                                .background(Color.White, RoundedCornerShape(16.dp))
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Cancel,
                                contentDescription = null,
                                tint = Color(0xFFE65100),
                                modifier = Modifier.size(44.dp),
                            )
                            Text(
                                text = state.errorMessage ?: "Không thể tải hồ sơ khách hàng.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFFE65100),
                                    fontWeight = FontWeight.Medium,
                                ),
                            )
                            Button(
                                onClick = { viewModel.refresh() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                                shape = RoundedCornerShape(8.dp),
                            ) {
                                Text("Thử lại", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }

                    // Không có tài liệu nào
                    state.documents.isEmpty() -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(24.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FolderOpen,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(56.dp),
                            )
                            Text(
                                text = "Chưa có tệp hồ sơ khách hàng",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF475569),
                                ),
                            )
                            Text(
                                text = "Hồ sơ của bạn sẽ hiển thị tại đây khi được công ty tải lên hệ thống.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF64748B),
                                ),
                            )
                        }
                    }

                    // Hiển thị trực tiếp tệp PDF
                    state.pdfFilePath != null -> {
                        PdfDocumentView(
                            filePath = state.pdfFilePath!!,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomerSummaryHeader(
    customerName: String,
    digiCode: String,
    fileName: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(22.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = customerName.ifBlank { "Khách hàng" },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        fontSize = 15.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (digiCode.isNotBlank()) "Mã KH: $digiCode" else "Đang tải mã KH...",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }
            if (fileName.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFEBEE),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Description,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(15.dp),
                        )
                        Text(
                            text = fileName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFFD32F2F),
                                fontWeight = FontWeight.SemiBold,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}
