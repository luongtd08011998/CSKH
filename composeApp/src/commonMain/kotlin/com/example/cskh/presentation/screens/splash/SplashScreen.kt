package com.example.cskh.presentation.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cskh.composeapp.generated.resources.Res
import cskh.composeapp.generated.resources.logocty1
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

private val splashGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFECF4FF),
        Color(0xFFF5F9FF),
        Color(0xFFFFFFFF),
    ),
)

/**
 * Splash screen hiển thị logo công ty với fade + scale animation.
 * Tự động gọi [onFinished] sau ~700ms (fade 300ms + hold 400ms).
 */
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.85f) }

    LaunchedEffect(Unit) {
        // Fade in + scale up mượt 350ms
        alpha.animateTo(1f, animationSpec = tween(durationMillis = 350))
        scale.animateTo(1f, animationSpec = tween(durationMillis = 350))
        // Giữ màn hình thêm 400ms trước khi chuyển
        delay(400)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(splashGradient),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .alpha(alpha.value)
                .scale(scale.value)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Logo công ty
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 12.dp,
                tonalElevation = 4.dp,
            ) {
                Image(
                    painter = painterResource(Res.drawable.logocty1),
                    contentDescription = "Logo công ty",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Công ty TNHH ",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = Color(0xFF5B8DB8),
                    letterSpacing = 1.2.sp,
                ),
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "CẤP NƯỚC TÓC TIÊN",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B5D99),
                    letterSpacing = 2.sp,
                ),
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Phục vụ tận tâm – Chất lượng bền vững",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF85A8C5),
                ),
                textAlign = TextAlign.Center,
            )
        }

        // Watermark nhẹ ở góc dưới
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(alpha.value * 0.5f),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Text(
                text = "v1.0",
                modifier = Modifier.padding(bottom = 32.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFFB0C4D8),
                ),
            )
        }
    }
}
