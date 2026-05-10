package com.example.cskh.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// ──────────────────────────────────────────────────────────────
// Màu sắc đồng bộ theme ngành nước
// ──────────────────────────────────────────────────────────────
private val WaterBlue = Color(0xFF1565C0)
private val WarningAmber = Color(0xFFE65100)
private val WarningBg = Color(0xFFFFF3E0)
private val LoadingBg = Color(0xFFF0F7FF)
private val LoadingTextColor = Color(0xFF5B8DB8)

/**
 * Status bar hiển thị trạng thái tải dữ liệu ở cuối màn hình.
 *
 * - [isLoading] = true + [isSlowConnection] = false → "Đang tải dữ liệu khách hàng…" + spinner
 * - [isSlowConnection] = true → "Kết nối máy chủ chậm" + icon + nút Thử lại
 * - Không hiển thị gì khi không loading
 */
@Composable
fun DataLoadingStatusBar(
    isLoading: Boolean,
    isSlowConnection: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = when {
            isSlowConnection -> LoadingBarState.SlowConnection
            isLoading -> LoadingBarState.Loading
            else -> LoadingBarState.Hidden
        },
        transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
        label = "loadingBarTransition",
        modifier = modifier,
    ) { state ->
        when (state) {
            LoadingBarState.Loading -> LoadingIndicatorBar()
            LoadingBarState.SlowConnection -> SlowConnectionBar(onRetry = onRetry)
            LoadingBarState.Hidden -> Spacer(Modifier)
        }
    }
}

private enum class LoadingBarState { Hidden, Loading, SlowConnection }

// ──────────────────────────────────────────────────────────────
// Loading bar – "Đang tải dữ liệu khách hàng…"
// ──────────────────────────────────────────────────────────────

@Composable
private fun LoadingIndicatorBar() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        color = LoadingBg,
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = WaterBlue,
                strokeWidth = 2.dp,
            )
            Text(
                text = "Đang tải dữ liệu khách hàng…",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = LoadingTextColor,
                    fontWeight = FontWeight.Medium,
                ),
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────
// Slow connection bar – cảnh báo + nút Thử lại
// ──────────────────────────────────────────────────────────────

@Composable
private fun SlowConnectionBar(onRetry: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        color = WarningBg,
        shadowElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.SignalWifiOff,
                    contentDescription = "Mất kết nối",
                    tint = WarningAmber,
                    modifier = Modifier.size(20.dp),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Kết nối máy chủ chậm",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = WarningAmber,
                        ),
                    )
                    Text(
                        text = "Vui lòng kiểm tra kết nối mạng của bạn",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF8D5E3A),
                        ),
                    )
                }
            }
            Button(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WarningAmber,
                    contentColor = Color.White,
                ),
            ) {
                Text(
                    text = "Thử lại",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                )
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────
// Helper alias for tween import (avoid name clash)
// ──────────────────────────────────────────────────────────────
private fun tween(durationMillis: Int) =
    androidx.compose.animation.core.tween<Float>(durationMillis = durationMillis)
