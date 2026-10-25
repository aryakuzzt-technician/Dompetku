package com.example.dompetku.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DompetLightColors = lightColorScheme(
    primary = Color(0xFF167D5A),
    onPrimary = Color.White,
    secondary = Color(0xFF4D8B73),
    background = Color(0xFFF5F7F5),
    surface = Color.White,
    onSurface = Color(0xFF202923),
    error = Color(0xFFD32F2F)
)

private val DompetDarkColors = darkColorScheme(
    primary = Color(0xFF79D6AE),
    secondary = Color(0xFF9BCDB7),
    background = Color(0xFF121714),
    surface = Color(0xFF1C241F),
    onSurface = Color(0xFFE5ECE7),
    error = Color(0xFFFF6B6B)
)

@Composable
fun DompetKuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DompetDarkColors
    } else {
        DompetLightColors
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
