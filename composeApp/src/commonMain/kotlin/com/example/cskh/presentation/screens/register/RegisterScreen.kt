package com.example.cskh.presentation.screens.register

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel

private val primaryBlue = Color(0xFF1976D2)
private val fieldBg = Color(0xFFF5F5F5)
private val fieldBorder = Color(0xFFE0E0E0)
private val buttonGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF42A5F5), Color(0xFF1E88E5)),
)
private const val TEMPLATE_URL = "https://beta.toctienltd.vn/2026_Giay_de_nghi_lap_dat_dong_ho_toctienltd.doc"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onNavigateHome: () -> Unit = {},
    viewModel: RegisterViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val fieldShape = RoundedCornerShape(16.dp)
    val uriHandler = LocalUriHandler.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Đăng ký lắp đặt") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                ),
            )

            val regTime = state.registrationTime
            if (regTime != null) {
                // ── Success screen ──
                SuccessContent(
                    registrationTime = regTime,
                    onNavigateHome = onNavigateHome,
                    onRegisterNew = { viewModel.resetForm() },
                    modifier = Modifier.weight(1f),
                )
            } else {
                // ── Form screen ──
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                        .navigationBarsPadding()
                        .padding(bottom = 24.dp),
                ) {
                    Text(
                        text = "Đăng Ký Lắp Đặt Nước Sạch",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF212121),
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Quý khách vui lòng điền đầy đủ thông tin dưới đây. Nhân viên của chúng tôi sẽ liên hệ lại trong vòng 24h để tiến hành khảo sát.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF757575),
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    val isNameError = state.name.isNotEmpty() && !state.name.trim().contains(" ")
                    Text(
                        text = "Họ và tên chủ hộ",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = if (isNameError) MaterialTheme.colorScheme.error else Color(0xFF424242),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = {
                            viewModel.onNameChange(it)
                            if (state.errorMessage != null) viewModel.clearError()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Nhập họ và tên", color = Color(0xFF9E9E9E)) },
                        singleLine = true,
                        shape = fieldShape,
                        isError = isNameError,
                        supportingText = if (isNameError) {
                            { Text("Họ và tên phải có ít nhất 2 từ", style = MaterialTheme.typography.labelSmall) }
                        } else null,
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Person, 
                                contentDescription = null, 
                                tint = if (isNameError) MaterialTheme.colorScheme.error else Color(0xFF9E9E9E)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryBlue,
                            unfocusedBorderColor = fieldBorder,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = fieldBg,
                            cursorColor = primaryBlue,
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            errorLeadingIconColor = MaterialTheme.colorScheme.error,
                            errorContainerColor = Color(0xFFFFFBFA),
                        ),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Số điện thoại
                    Text(
                        text = "Số điện thoại liên hệ",
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
                        leadingIcon = {
                            Icon(Icons.Filled.Phone, contentDescription = null, tint = Color(0xFF9E9E9E))
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

                    // Email
                    Text(
                        text = "Địa chỉ Email (nếu có)",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = Color(0xFF424242),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = viewModel::onEmailChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Nhập email", color = Color(0xFF9E9E9E)) },
                        singleLine = true,
                        shape = fieldShape,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        leadingIcon = {
                            Icon(Icons.Filled.Email, contentDescription = null, tint = Color(0xFF9E9E9E))
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

                    // Địa chỉ lắp đặt
                    Text(
                        text = "Địa chỉ lắp đặt đồng hồ",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = Color(0xFF424242),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.waterMeterAddress,
                        onValueChange = viewModel::onAddressChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Nhập địa chỉ lắp đặt", color = Color(0xFF9E9E9E)) },
                        minLines = 2,
                        maxLines = 4,
                        shape = fieldShape,
                        leadingIcon = {
                            Icon(Icons.Filled.Home, contentDescription = null, tint = Color(0xFF9E9E9E))
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryBlue,
                            unfocusedBorderColor = fieldBorder,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = fieldBg,
                            cursorColor = primaryBlue,
                        ),
                    )

                    // Error message
                    state.errorMessage?.let { msg ->
                        Spacer(modifier = Modifier.height(12.dp))
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

                    Spacer(modifier = Modifier.height(24.dp))

                    // Submit button
                    val canSubmit = state.name.isNotBlank() && state.phone.isNotBlank() && state.waterMeterAddress.isNotBlank() && !state.isSubmitting
                    Button(
                        onClick = { viewModel.submit() },
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
                                .background(buttonGradient, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (state.isSubmitting) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(22.dp),
                                        strokeWidth = 2.dp,
                                        color = Color.White,
                                    )
                                    Text("Đang gửi...", color = Color.White)
                                }
                            } else {
                                Text(
                                    "Gửi yêu cầu",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Thông tin hồ sơ
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF5F5F5),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Hồ sơ chuẩn bị sẵn:",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = Color(0xFF424242),
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            val docs = listOf(
                                "Đơn xin lắp đặt (Tải về mẫu đơn tại đây)",
                                "Giấy chứng nhận quyền sử dụng đất chính chủ",
                                "Căn cước công dân của người đứng tên trên GCNQSDĐ",
                                "Giấy phép xây dựng (Nếu đất nằm ngoài khu TĐC và Khu đã được cấp phép)",
                                "Đối với Doanh Nghiệp: Giấy phép ĐKKD, CCCD người đại diện",
                                "Hợp đồng thuê đất (Nếu là đất thuê)"
                            )
                            
                            docs.forEachIndexed { index, item ->
                                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text(
                                        text = "•",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = primaryBlue,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    if (index == 0) {
                                        Row {
                                            Text(
                                                text = "Đơn xin lắp đặt (",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF616161)
                                            )
                                            Text(
                                                text = "Tải về mẫu đơn tại đây",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = primaryBlue,
                                                    textDecoration = TextDecoration.Underline
                                                ),
                                                modifier = Modifier.clickable { uriHandler.openUri(TEMPLATE_URL) }
                                            )
                                            Text(
                                                text = ")",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF616161)
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = item,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF616161),
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Lưu ý quan trọng:",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFD32F2F),
                            )
                            Text(
                                text = "Quý khách vui lòng chuẩn bị đầy đủ các loại giấy tờ nêu trên để cung cấp cho nhân viên khảo sát. Trường hợp hồ sơ chưa đầy đủ, chúng tôi rất tiếc chưa thể tiếp nhận và xử lý yêu cầu.",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF757575),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PaddingValues() = androidx.compose.foundation.layout.PaddingValues()

@Composable
private fun SuccessContent(
    registrationTime: String,
    onNavigateHome: () -> Unit,
    onRegisterNew: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 20.dp, bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Check icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color(0xFF4CAF50), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(44.dp),
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Đăng Ký Thành Công!",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = primaryBlue,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Sau khi gửi yêu cầu, hệ thống sẽ tự động xử lý và chuyển cho bộ phận kỹ thuật. Chúng tôi sẽ liên hệ lại với quý khách theo số điện thoại đã cung cấp để hướng dẫn các bước tiếp theo.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF424242),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Timestamp card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFE3F2FD),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Thời gian ghi nhận:",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                    color = Color(0xFF616161),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = registrationTime,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = primaryBlue,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Document requirements
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF9F9F9), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Quý khách vui lòng chuẩn bị hồ sơ gồm:",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF212121),
            )
            Spacer(modifier = Modifier.height(12.dp))

            val docs = listOf(
                "Đơn xin lắp đặt: Tải về mẫu đơn tại đây",
                "Giấy chứng nhận quyền sử dụng đất chính chủ",
                "Căn cước công dân của người đứng tên trên GCNQSDĐ",
                "Giấy phép xây dựng (Nếu đất nằm ngoài khu TĐC và Khu đã được cấp phép)",
                "Đối với Doanh Nghiệp: Giấy phép ĐKKD, CCCD người đại diện",
                "Hợp đồng thuê đất (Nếu là đất thuê)"
            )

            docs.forEachIndexed { index, item ->
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text("•", color = primaryBlue, modifier = Modifier.padding(end = 8.dp))
                    if (index == 0) {
                        Row {
                            Text(
                                text = "Đơn xin lắp đặt: ",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF424242)
                            )
                            Text(
                                text = "Tải về mẫu đơn tại đây",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = primaryBlue,
                                    textDecoration = TextDecoration.Underline
                                ),
                                modifier = Modifier.clickable { uriHandler.openUri(TEMPLATE_URL) }
                            )
                        }
                    } else {
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF424242),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Important Note
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFFEBEE),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Lưu ý quan trọng",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFC62828)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Kính đề nghị Quý khách chuẩn bị đầy đủ các loại giấy tờ nêu trên để cung cấp cho nhân viên khảo sát khi đến làm việc. Trường hợp hồ sơ hoặc thông tin chưa đầy đủ, chúng tôi rất tiếc chưa thể tiếp nhận và xử lý yêu cầu của Quý khách.",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFC62828),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                onClick = onNavigateHome,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
            ) {
                Text("Về trang chủ", color = Color.White)
            }
            OutlinedButton(
                onClick = onRegisterNew,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Đăng ký mới")
            }
        }
    }
}
