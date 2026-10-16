package com.example.cskh.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun PdfDocumentView(
    filePath: String,
    modifier: Modifier = Modifier,
)
