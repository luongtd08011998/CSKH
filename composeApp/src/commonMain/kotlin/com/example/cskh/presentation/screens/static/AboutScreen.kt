package com.example.cskh.presentation.screens.static

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.foundation.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import cskh.composeapp.generated.resources.Res
import cskh.composeapp.generated.resources.logocty1

private const val CompanyName = "Công ty TNHH cấp nước Tóc Tiên"
private const val CompanyAddress = "Ấp 6, Xã Châu Pha, Thành phố Hồ Chí Minh, Việt Nam."
private const val CompanyDirector = "Phan Thanh Hải"
private const val CompanyTaxCode = "3500815711"
private const val CompanyBankAccount = "008.1000.127.995"
private const val CompanyBankName = "Ngân hàng VIETCOMBANK BR-VT PGD số 3."
private const val CompanyPhoneDisplay = "0254 3 894 894 - 0865379119"
private const val CompanyPhoneTel = "02543894894"
private const val CompanyEmail = "office@toctienltd.vn"
private const val CompanyWebsite = "toctienltd.vn"

private val headerBlueStart = Color(0xFF42A5F5)
private val headerBlueEnd = Color(0xFF1E88E5)
private val pageBgTop = Color(0xFFFAFAFA)
private val pageBgBottom = Color(0xFFF0F0F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    val clipboard = LocalClipboardManager.current
    val mapsUrl = "https://www.google.com/maps/search?q=" + CompanyAddress.replace(" ", "+")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Giới thiệu") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                        )
                    }
                },
            )
        },
        containerColor = Color.Transparent,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = listOf(pageBgTop, pageBgBottom)))
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Transparent,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(colors = listOf(headerBlueStart, headerBlueEnd)),
                        )
                        .statusBarsPadding()
                        .padding(horizontal = 18.dp, vertical = 18.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Surface(
                            modifier = Modifier.size(56.dp),
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 6.dp,
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.logocty1),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(2.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = CompanyName,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Thông tin liên hệ & pháp lý",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f),
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Liên hệ",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = Color(0xFF212121),
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        ContactRow(
                            icon = Icons.Filled.LocationOn,
                            label = "Địa chỉ",
                            value = CompanyAddress,
                            onClick = { uriHandler.openUri(mapsUrl) },
                            onLongPressCopy = { clipboard.setText(AnnotatedString(CompanyAddress)) },
                        )
                        HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(vertical = 10.dp))

                        ContactRow(
                            icon = Icons.Filled.Phone,
                            label = "Điện thoại",
                            value = CompanyPhoneDisplay,
                            onClick = { uriHandler.openUri("tel:$CompanyPhoneTel") },
                            onLongPressCopy = { clipboard.setText(AnnotatedString(CompanyPhoneDisplay)) },
                        )
                        HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(vertical = 10.dp))

                        ContactRow(
                            icon = Icons.Filled.Email,
                            label = "Email",
                            value = CompanyEmail,
                            onClick = { uriHandler.openUri("mailto:$CompanyEmail") },
                            onLongPressCopy = { clipboard.setText(AnnotatedString(CompanyEmail)) },
                        )
                        HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(vertical = 10.dp))

                        ContactRow(
                            icon = Icons.Filled.Language,
                            label = "Website",
                            value = CompanyWebsite,
                            onClick = { uriHandler.openUri("https://$CompanyWebsite") },
                            onLongPressCopy = { clipboard.setText(AnnotatedString(CompanyWebsite)) },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Thông tin doanh nghiệp",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = Color(0xFF212121),
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        InfoLine(label = "Giám đốc", value = CompanyDirector)
                        HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(vertical = 10.dp))
                        InfoLine(label = "Mã số thuế", value = CompanyTaxCode)
                        HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(vertical = 10.dp))
                        InfoLine(label = "Số tài khoản", value = "$CompanyBankAccount $CompanyBankName")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { clipboard.setText(AnnotatedString(buildContactSummary())) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                ) {
                    Text("Sao chép thông tin liên hệ", color = Color.White)
                }
            }
        }
    }
}

private fun buildContactSummary(): String = buildString {
    appendLine(CompanyName)
    appendLine("Địa chỉ: $CompanyAddress")
    appendLine("Giám đốc: $CompanyDirector")
    appendLine("Mã số thuế: $CompanyTaxCode")
    appendLine("Số tài khoản: $CompanyBankAccount $CompanyBankName")
    appendLine("Điện thoại: $CompanyPhoneDisplay")
    appendLine("Email: $CompanyEmail")
    append("Website: $CompanyWebsite")
}.trim()

@Composable
private fun ContactRow(
    icon: ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit,
    onLongPressCopy: () -> Unit,
) {
    // Copy hook is kept for future combinedClickable; avoid unused warning now.
    @Suppress("UNUSED_EXPRESSION")
    onLongPressCopy

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            // Best-effort "copy": long press isn't available without combinedClickable; keep simple click-to-open.
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFFE3F2FD)) {
            Icon(icon, contentDescription = null, tint = Color(0xFF1976D2), modifier = Modifier.padding(10.dp).size(22.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = Color(0xFF757575))
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF212121))
        }
        Text(
            text = "Mở",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
            color = Color(0xFF1976D2),
            modifier = Modifier.clickable { onClick() },
        )
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color(0xFF757575))
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF212121))
    }
}
