package com.example.cskh.presentation.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val WaterBlue = Color(0xFF0B6E99)
private val WaterBlueLight = Color(0xFF4FA3C7)
private val SurfaceTint = Color(0xFFE8F4FA)
private val WaterDark = Color(0xFF06344A)
private val OnDarkPrimary = Color(0xFF003544)

private val LightColors = lightColorScheme(
    primary = WaterBlue,
    onPrimary = Color.White,
    primaryContainer = SurfaceTint,
    onPrimaryContainer = WaterDark,
    secondary = WaterBlueLight,
    onSecondary = OnDarkPrimary,
    tertiary = Color(0xFF2A7D6B),
    background = Color(0xFFF7FBFD),
    surface = Color.White,
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = SurfaceTint,
    outline = Color(0xFF6F797E),
)

private val DarkColors = darkColorScheme(
    primary = WaterBlueLight,
    onPrimary = OnDarkPrimary,
    primaryContainer = WaterDark,
    onPrimaryContainer = SurfaceTint,
    secondary = WaterBlue,
    onSecondary = Color.White,
    background = Color(0xFF0F1417),
    surface = Color(0xFF1A1F23),
    onSurface = Color(0xFFE1E3E5),
)

@Composable
fun CskhTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val scheme: ColorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = scheme,
        typography = Typography(),
        content = content,
    )
}
