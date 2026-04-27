package com.example.cskh.platform

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import com.example.cskh.domain.usecase.UserFormPreferencesUseCase
import com.example.cskh.data.session.SessionManager
import org.koin.compose.koinInject

@Composable
actual fun NotificationPermissionGate() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val prefs = koinInject<UserFormPreferencesUseCase>()
    val sessionManager = koinInject<SessionManager>()
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { _ ->
        // Regardless of grant/deny, don't show again automatically.
        prefs.setNotificationPermissionPrompted(true)
        showDialog = false
    }

    val hasPermission =
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    val token by sessionManager.tokenFlow.collectAsState()
    val isLoggedIn = !token.isNullOrBlank()

    LaunchedEffect(isLoggedIn, hasPermission) {
        if (!isLoggedIn) {
            showDialog = false
            return@LaunchedEffect
        }
        val alreadyPrompted = prefs.isNotificationPermissionPrompted()
        if (!alreadyPrompted && !hasPermission) showDialog = true
    }

    if (!showDialog) return

    AlertDialog(
        onDismissRequest = {
            prefs.setNotificationPermissionPrompted(true)
            showDialog = false
        },
        title = { Text("Bật thông báo?") },
        text = {
            Text("Cho phép ứng dụng gửi thông báo để bạn không bỏ lỡ hóa đơn mới, bài viết và trạng thái hóa đơn.")
        },
        confirmButton = {
            Button(
                onClick = {
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                },
            ) { Text("Cho phép") }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    prefs.setNotificationPermissionPrompted(true)
                    showDialog = false
                },
            ) { Text("Để sau") }
        },
    )
}

