package com.example.cskh.presentation.screens.login

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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.foundation.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cskh.presentation.CompanyBranding
import com.example.cskh.presentation.components.AuthLoadingOverlay
import org.koin.compose.viewmodel.koinViewModel
import org.jetbrains.compose.resources.painterResource
import cskh.composeapp.generated.resources.Res
import cskh.composeapp.generated.resources.logocty1
import androidx.compose.animation.core.animateFloatAsState

private val bgGradientTop = Color(0xFFE3F2FD)
private val bgGradientMid = Color(0xFFFFFFFF)
private val bgGradientBottom = Color(0xFFE8F4FC)
private val fieldBg = Color(0xFFF5F5F5)
private val fieldBorder = Color(0xFFE0E0E0)
private val logoGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF42A5F5), Color(0xFF1565C0)),
)
private val buttonGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF42A5F5), Color(0xFF1E88E5)),
)
private val blob1 = Color(0x3390CAF9)
private val blob2 = Color(0x3364B5F6)

private val logoPlateBrush = Brush.linearGradient(
    colors = listOf(Color(0x3364B5F6), Color(0x331E88E5)),
)
private val buttonDisabledBrush = Brush.horizontalGradient(
    colors = listOf(Color(0xFFBDBDBD), Color(0xFF9E9E9E)),
)

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
    onNavigateRegister: () -> Unit = {},
    viewModel: LoginViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    var phoneVisible by remember { mutableStateOf(false) }

    val primaryBlue = Color(0xFF1976D2)
    val fieldShape = RoundedCornerShape(16.dp)
    val cardShape = RoundedCornerShape(28.dp)

    // Animation cho card khi đang loading
    val cardScale by animateFloatAsState(
        targetValue = if (state.isLoading) 0.97f else 1f,
        animationSpec = tween(300),
        label = "cardScale",
    )

    val infinite = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infinite.animateFloat(
        initialValue = 0.65f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "dot",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(bgGradientTop, bgGradientMid, bgGradientBottom),
                ),
            ),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 24.dp, y = (-40).dp)
                .size(220.dp)
                .clip(CircleShape)
                .background(blob1),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-48).dp, y = 32.dp)
                .size(280.dp)
                .clip(CircleShape)
                .background(blob2),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 440.dp)
                .align(Alignment.TopCenter)
                .verticalScroll(scrollState)
                .statusBarsPadding()
                .padding(top = 28.dp)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.size(96.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(Res.drawable.logocty1),
                    contentDescription = null,
                    modifier = Modifier
                        .size(86.dp)
                        .shadow(elevation = 8.dp, shape = CircleShape)
                        .background(Color.White, shape = CircleShape)
                        .padding(3.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = CompanyBranding.NAME,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF212121),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Hệ thống quản lý khách hàng",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF757575),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(36.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer(
                        scaleX = cardScale,
                        scaleY = cardScale,
                        rotationZ = if (state.isLoading) 0.5f else 0f
                    )
                    .shadow(
                        elevation = if (state.isLoading) 4.dp else 12.dp,
                        shape = cardShape,
                    ),
                shape = cardShape,
                color = Color.White,
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 22.dp),
                ) {
                    Text(
                        text = "Đăng nhập",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Color(0xFF212121),
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Vui lòng nhập thông tin để tiếp tục",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF757575),
                    )
                    Spacer(modifier = Modifier.height(22.dp))

                    Text(
                        text = "Mã khách hàng",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = Color(0xFF424242),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.digiCode,
                        onValueChange = viewModel::onDigiCodeChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Nhập mã khách hàng", color = Color(0xFF9E9E9E)) },
                        singleLine = true,
                        shape = fieldShape,
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Tag,
                                contentDescription = null,
                                tint = Color(0xFF9E9E9E),
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryBlue,
                            unfocusedBorderColor = fieldBorder,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = fieldBg,
                            cursorColor = primaryBlue,
                        ),
                    )

                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "Số điện thoại",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = Color(0xFF424242),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.phone,
                        onValueChange = viewModel::onPhoneChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Nhập số điện thoại", color = Color(0xFF9E9E9E)) },
                        singleLine = true,
                        shape = fieldShape,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        visualTransformation = if (phoneVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Phone,
                                contentDescription = null,
                                tint = Color(0xFF9E9E9E),
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { phoneVisible = !phoneVisible }) {
                                Icon(
                                    imageVector = if (phoneVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = if (phoneVisible) "Ẩn" else "Hiện",
                                    tint = Color(0xFF9E9E9E),
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryBlue,
                            unfocusedBorderColor = fieldBorder,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = fieldBg,
                            cursorColor = primaryBlue,
                        ),
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f),
                        ) {
                            Checkbox(
                                checked = state.rememberLogin,
                                onCheckedChange = viewModel::onRememberLoginChange,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = primaryBlue,
                                    checkmarkColor = Color.White,
                                ),
                            )
                            Text(
                                text = "Ghi nhớ đăng nhập",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF616161),
                            )
                        }
                        TextButton(
                            onClick = onNavigateRegister,
                        ) {
                            Text(
                                "Đăng ký lắp đặt trực tuyến",
                                style = MaterialTheme.typography.bodySmall,
                                color = primaryBlue,
                            )
                        }
                    }

                    state.errorMessage?.let { msg ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                        ) {
                            Text(
                                text = msg,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    val canSubmit = state.digiCode.isNotBlank() && state.phone.isNotBlank()
                    Button(
                        onClick = { viewModel.login(onLoggedIn) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        enabled = canSubmit,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            contentColor = Color.White,
                            disabledContentColor = Color.White,
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 2.dp,
                            disabledElevation = 0.dp,
                        ),
                        contentPadding = PaddingValues(),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = when {
                                        state.isLoading -> buttonGradient
                                        canSubmit -> buttonGradient
                                        else -> buttonDisabledBrush
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (state.isLoading) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(22.dp),
                                        strokeWidth = 2.dp,
                                        color = Color.White,
                                    )
                                    Text("Đang đăng nhập...", color = Color.White)
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Text(
                                        "Đăng nhập",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(22.dp),
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(top = 18.dp),
                        color = Color(0xFFEEEEEE),
                    )
                    Text(
                        text = "Hotline: ${CompanyBranding.PHONE_DISPLAY}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 2.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF757575),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // ── Giai đoạn Loading Đăng nhập ──
        AuthLoadingOverlay(
            isLoading = state.isLoading,
            isSlowConnection = state.isSlowConnection,
        )
    }
}
