package com.example.cskh.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ──────────────────────────────────────────────────────────────
// Màu sắc skeleton – đồng bộ theme ngành nước
// ──────────────────────────────────────────────────────────────
private val SkeletonBase = Color(0xFFDDE6EF)
private val SkeletonHighlight = Color(0xFFF3F7FB)
private val SkeletonDark = Color(0xFFCDD8E3)

// ──────────────────────────────────────────────────────────────
// Shimmer Engine
// ──────────────────────────────────────────────────────────────

/**
 * Trả về [Brush] shimmer gradient chạy ngang, dùng chung cho mọi skeleton.
 */
@Composable
fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateX by transition.animateFloat(
        initialValue = -600f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerTranslate",
    )
    return Brush.linearGradient(
        colors = listOf(
            SkeletonBase,
            SkeletonHighlight,
            SkeletonBase,
        ),
        start = Offset(translateX, 0f),
        end = Offset(translateX + 600f, 0f),
    )
}

// ──────────────────────────────────────────────────────────────
// Primitive Skeleton Box
// ──────────────────────────────────────────────────────────────

@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    height: Dp = 16.dp,
    cornerRadius: Dp = 8.dp,
    brush: Brush = shimmerBrush(),
) {
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(brush),
    )
}

@Composable
fun SkeletonCircle(
    size: Dp,
    brush: Brush = shimmerBrush(),
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(brush),
    )
}

// ──────────────────────────────────────────────────────────────
// HomeHeroSkeleton – phần header gradient (tên KH, mã KH)
// ──────────────────────────────────────────────────────────────

@Composable
fun HomeHeroSkeleton() {
    // Header gradient background – giống HomeHero thật
    val headerBrush = Brush.linearGradient(
        colors = listOf(Color(0xFF1565C0), Color(0xFF0D47A1), Color(0xFF0891B2)),
    )
    // Skeleton items trên nền đậm dùng màu nhạt hơn
    val heroBrush = Brush.linearGradient(
        colors = listOf(
            Color(0x40FFFFFF),
            Color(0x80FFFFFF),
            Color(0x40FFFFFF),
        ),
        start = Offset(-600f, 0f),
        end = Offset(600f, 0f),
    )
    val transition = rememberInfiniteTransition(label = "heroShimmer")
    val translateX by transition.animateFloat(
        initialValue = -600f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "heroTranslate",
    )
    val heroDynamicBrush = Brush.linearGradient(
        colors = listOf(
            Color(0x30FFFFFF),
            Color(0x70FFFFFF),
            Color(0x30FFFFFF),
        ),
        start = Offset(translateX, 0f),
        end = Offset(translateX + 600f, 0f),
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(headerBrush)
            .padding(horizontal = 16.dp, vertical = 18.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Row: logo + tiêu đề công ty (skeleton)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SkeletonCircle(size = 54.dp, brush = heroDynamicBrush)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(heroDynamicBrush),
                    )
                    Box(
                        modifier = Modifier
                            .width(160.dp)
                            .height(18.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(heroDynamicBrush),
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Card thông tin khách hàng skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .padding(14.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Label "Khách hàng"
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(heroDynamicBrush),
                    )
                    // Tên khách hàng (lớn hơn)
                    Box(
                        modifier = Modifier
                            .width(200.dp)
                            .height(22.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(heroDynamicBrush),
                    )
                    Spacer(Modifier.height(2.dp))
                    // Mã KH + địa chỉ
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        Box(
                            modifier = Modifier
                                .width(90.dp)
                                .height(13.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(heroDynamicBrush),
                        )
                        Box(
                            modifier = Modifier
                                .width(130.dp)
                                .height(13.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(heroDynamicBrush),
                        )
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────
// CurrentInvoiceCardSkeleton – card hóa đơn tháng này
// ──────────────────────────────────────────────────────────────

@Composable
fun CurrentInvoiceCardSkeleton() {
    val brush = shimmerBrush()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Thanh màu bên trái
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(160.dp)
                    .background(SkeletonDark),
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                // Label "Hóa đơn tháng X"
                SkeletonBox(modifier = Modifier.width(140.dp), height = 14.dp)
                // Số tiền lớn
                SkeletonBox(modifier = Modifier.width(200.dp), height = 32.dp, cornerRadius = 10.dp)
                // Hạn thanh toán
                SkeletonBox(modifier = Modifier.fillMaxWidth(0.6f), height = 14.dp)
                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(SkeletonBase),
                )
                // Lượng nước
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SkeletonBox(modifier = Modifier.width(120.dp), height = 12.dp)
                    SkeletonBox(modifier = Modifier.width(80.dp), height = 16.dp)
                }
                Spacer(Modifier.height(4.dp))
                // Button
                SkeletonBox(
                    modifier = Modifier.fillMaxWidth(),
                    height = 48.dp,
                    cornerRadius = 14.dp,
                    brush = Brush.linearGradient(listOf(Color(0xFFBDD6F0), Color(0xFFCDE4F8))),
                )
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────
// MenuGridSkeleton – 4 ô menu chức năng
// ──────────────────────────────────────────────────────────────

@Composable
fun MenuGridSkeleton() {
    val brush = shimmerBrush()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            repeat(2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    repeat(2) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(brush),
                            )
                            SkeletonBox(modifier = Modifier.width(50.dp), height = 12.dp, brush = brush)
                        }
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────
// RecentInvoicesSkeleton – lịch sử thanh toán (3 hàng)
// ──────────────────────────────────────────────────────────────

@Composable
fun RecentInvoicesSkeleton() {
    val brush = shimmerBrush()
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        // Title row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SkeletonBox(modifier = Modifier.width(130.dp), height = 20.dp, brush = brush)
            SkeletonBox(modifier = Modifier.width(70.dp), height = 14.dp, brush = brush)
        }

        Spacer(Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                repeat(3) { idx ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            SkeletonBox(modifier = Modifier.width(80.dp), height = 15.dp, brush = brush)
                            SkeletonBox(modifier = Modifier.width(120.dp), height = 12.dp, brush = brush)
                        }
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            SkeletonBox(modifier = Modifier.width(90.dp), height = 15.dp, brush = brush)
                            SkeletonBox(modifier = Modifier.width(70.dp), height = 20.dp, cornerRadius = 999.dp, brush = brush)
                        }
                    }
                    if (idx < 2) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(SkeletonBase),
                        )
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────
// HomeSkeletonContent – toàn bộ skeleton layout của HomeScreen
// ──────────────────────────────────────────────────────────────

@Composable
fun HomeSkeletonContent() {
    Column(modifier = Modifier.fillMaxWidth()) {
        HomeHeroSkeleton()

        Box {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 0.dp)
                    .align(Alignment.TopCenter)
                    // offset giống HomeScreen thật
                    .then(Modifier.padding(top = 0.dp)),
            ) {
                // Đặt card hóa đơn có offset âm để nổi đè lên header
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Spacer(Modifier.height((-24 + 16).dp)) // bù offset giống bản thật
            CurrentInvoiceCardSkeleton()
            Spacer(Modifier.height(18.dp))

            // Title "Menu chức năng"
            SkeletonBox(modifier = Modifier.width(150.dp), height = 22.dp)
            Spacer(Modifier.height(12.dp))
            MenuGridSkeleton()

            Spacer(Modifier.height(18.dp))
            RecentInvoicesSkeleton()
            Spacer(Modifier.height(18.dp))
        }
    }
}
