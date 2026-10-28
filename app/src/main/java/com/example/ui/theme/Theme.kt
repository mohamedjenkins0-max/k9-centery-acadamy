package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val K9DarkColorScheme = darkColorScheme(
    primary = RoyalBlueLight,
    onPrimary = Color.White,
    primaryContainer = RoyalBlueDark,
    onPrimaryContainer = Color.White,
    secondary = CenturyGold,
    onSecondary = NavyDarkest,
    secondaryContainer = NavyCardElevated,
    onSecondaryContainer = CenturyGoldLight,
    tertiary = LaurelGreenLight,
    onTertiary = Color.White,
    background = NavyDarkest,
    onBackground = SurfaceTextWhite,
    surface = NavyDark,
    onSurface = SurfaceTextWhite,
    surfaceVariant = NavyCard,
    onSurfaceVariant = SurfaceTextMuted,
    outline = BorderSubtle,
    error = AlertRed,
    onError = Color.White
)

private val K9LightColorScheme = lightColorScheme(
    primary = RoyalBluePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E3FF),
    onPrimaryContainer = RoyalBlueDark,
    secondary = CenturyGoldDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF3CD),
    onSecondaryContainer = Color(0xFF5A4400),
    tertiary = LaurelGreen,
    onTertiary = Color.White,
    background = Color(0xFFF6F8FC),
    onBackground = Color(0xFF101928),
    surface = Color.White,
    onSurface = Color(0xFF101928),
    surfaceVariant = Color(0xFFE6EDF8),
    onSurfaceVariant = Color(0xFF384B66),
    outline = Color(0xFFBAC7DD),
    error = AlertRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek midnight tactical academy dark theme
    dynamicColor: Boolean = false, // Keep prestigious academy brand identity consistent
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) K9DarkColorScheme else K9LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
