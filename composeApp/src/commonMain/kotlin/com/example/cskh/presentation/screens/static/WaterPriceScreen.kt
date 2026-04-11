package com.example.cskh.presentation.screens.static

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cskh.presentation.CompanyBranding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterPriceScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bảng giá nước") },
                navigationIcon = {
                    Text(
                        "←",
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { onBack() },
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            Text(
                CompanyBranding.NAME,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                "Biểu giá và định mức được áp dụng theo quy định của địa phương và được niêm yết công khai tại trụ sở công ty.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp),
            )
            Text(
                "Để biết chi tiết biểu giá theo từng nhóm đối tượng (sinh hoạt, sản xuất, kinh doanh…), " +
                    "quý khách vui lòng liên hệ trực tiếp qua số điện thoại hoặc email trên trang chủ ứng dụng.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}
