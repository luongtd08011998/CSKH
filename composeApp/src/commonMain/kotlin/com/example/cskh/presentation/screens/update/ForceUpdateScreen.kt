package com.example.cskh.presentation.screens.update

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ForceUpdateScreen(
    storeUrl: String,
    releaseNotes: String?,
) {
    val uriHandler = LocalUriHandler.current

    val alpha = remember { Animatable(0f) }
    val translateY = remember { Animatable(40f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
        translateY.animateTo(0f, tween(500, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0B5D99), Color(0xFF1A82CC), Color(0xFF5B8DB8)),
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .alpha(alpha.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Icon cập nhật
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.15f),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.SystemUpdate,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(52.dp),
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = "Cần cập nhật ứng dụng",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.5.sp,
                ),
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Phiên bản hiện tại không còn được hỗ trợ.\nVui lòng cập nhật để tiếp tục sử dụng.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.85f),
                    lineHeight = 22.sp,
                ),
                textAlign = TextAlign.Center,
            )

            // Nếu có release notes thì hiện
            if (!releaseNotes.isNullOrBlank()) {
                Spacer(Modifier.height(20.dp))

                Surface(
                    color = Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    ) {
                        Text(
                            text = "Có gì mới?",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                            ),
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = releaseNotes,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.8f),
                                lineHeight = 20.sp,
                            ),
                        )
                    }
                }
            }

            Spacer(Modifier.height(36.dp))

            // Nút cập nhật ngay
            Button(
                onClick = {
                    runCatching { uriHandler.openUri(storeUrl) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF0B5D99),
                ),
            ) {
                Text(
                    text = "Cập nhật ngay",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    ),
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Bạn bắt buộc phải cập nhật để tiếp tục.",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.55f),
                ),
                textAlign = TextAlign.Center,
            )
        }
    }
}
