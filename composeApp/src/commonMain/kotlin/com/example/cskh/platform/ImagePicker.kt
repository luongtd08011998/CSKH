package com.example.cskh.platform

import androidx.compose.runtime.Composable

data class PickedImage(
    val uri: String,
    val name: String,
)

interface ImagePicker {
    fun pickImages(max: Int)
    fun takePhoto()
}

@Composable
expect fun rememberImagePicker(
    onResult: (List<PickedImage>) -> Unit,
    onError: (String) -> Unit = {},
): ImagePicker

