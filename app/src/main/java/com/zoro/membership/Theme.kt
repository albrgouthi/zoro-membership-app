package com.zoro.membership

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ZoroGreen = Color(0xFF1B8A5A)
private val ZoroGreenDark = Color(0xFF0E5C3A)

private val LightColors = lightColorScheme(
    primary = ZoroGreen,
    secondary = ZoroGreenDark
)

private val DarkColors = darkColorScheme(
    primary = ZoroGreen,
    secondary = ZoroGreenDark
)

@Composable
fun ZoroTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) DarkColors else LightColors
    MaterialTheme(colorScheme = colors, content = content)
}
