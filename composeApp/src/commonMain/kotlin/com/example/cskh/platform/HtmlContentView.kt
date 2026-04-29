package com.example.cskh.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun HtmlContentView(
    html: String,
    modifier: Modifier = Modifier,
)
