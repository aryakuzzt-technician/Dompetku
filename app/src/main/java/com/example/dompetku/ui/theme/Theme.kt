package com.example.dompetku.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1E88E5),
    secondary = Color(0xFF26A69A),
    background = Color(0xFFF5F7FA),
    surface = Color.White
)

@Composable
fun DompetKuTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}

