package com.zoro.membership

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Zoro palette: deep emerald (trust, savings) + warm gold (membership/reward),
// deliberately avoiding the generic cream+terracotta / near-black+neon defaults.
private val Emerald = Color(0xFF15754F)
private val EmeraldDark = Color(0xFF0E5C3A)
private val Gold = Color(0xFFC9A227)
private val Charcoal = Color(0xFF23241F)
private val Cream = Color(0xFFF7F5EF)

private val LightColors = lightColorScheme(
    primary = Emerald,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCEFE5),
    secondary = Gold,
    onSecondary = Color(0xFF3A2E00),
    tertiary = Charcoal,
    background = Cream,
    surface = Color.White,
    surfaceVariant = Color(0xFFEDEAE1)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF3FBE8C),
    onPrimary = Color(0xFF00301D),
    primaryContainer = EmeraldDark,
    secondary = Gold,
    onSecondary = Color(0xFF3A2E00),
    tertiary = Cream,
    background = Color(0xFF14150F),
    surface = Color(0xFF1C1D17),
    surfaceVariant = Color(0xFF2A2B23)
)

private val ZoroTypography = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 30.sp, letterSpacing = (-0.5).sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, letterSpacing = (-0.2).sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp, letterSpacing = 0.3.sp)
)

@Composable
fun ZoroTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) DarkColors else LightColors
    MaterialTheme(colorScheme = colors, typography = ZoroTypography, content = content)
}
