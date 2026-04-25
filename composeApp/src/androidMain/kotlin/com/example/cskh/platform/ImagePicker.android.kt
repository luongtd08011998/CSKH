package com.example.cskh.platform

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@Composable
actual fun rememberImagePicker(
    onResult: (List<PickedImage>) -> Unit,
    onError: (String) -> Unit,
): ImagePicker {
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
    ) { uris: List<Uri> ->
        try {
            if (uris.isEmpty()) return@rememberLauncherForActivityResult
            val picked = uris.map { uri ->
                PickedImage(
                    uri = uri.toString(),
                    name = queryDisplayName(context, uri) ?: "image",
                )
            }
            onResult(picked)
        } catch (t: Throwable) {
            onError(t.message ?: "Không chọn được ảnh")
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
    ) { bmp: Bitmap? ->
        try {
            if (bmp == null) return@rememberLauncherForActivityResult
            val file = saveBitmapToCache(context, bmp)
            onResult(
                listOf(
                    PickedImage(
                        uri = Uri.fromFile(file).toString(),
                        name = file.name,
                    ),
                ),
            )
        } catch (t: Throwable) {
            onError(t.message ?: "Không chụp được ảnh")
        }
    }

    return remember {
        object : ImagePicker {
            override fun pickImages(max: Int) {
                // max is enforced by the system picker UI on Android 13+; older devices may ignore.
                galleryLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )
            }

            override fun takePhoto() {
                cameraLauncher.launch(null)
            }
        }
    }
}

private fun queryDisplayName(context: Context, uri: Uri): String? {
    return runCatching {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex < 0) return@use null
            if (!cursor.moveToFirst()) return@use null
            cursor.getString(nameIndex)
        }
    }.getOrNull()
}

private fun saveBitmapToCache(context: Context, bitmap: Bitmap): File {
    val dir = File(context.cacheDir, "phananh").apply { mkdirs() }
    val file = File(dir, "photo_${UUID.randomUUID()}.jpg")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
    }
    return file
}

