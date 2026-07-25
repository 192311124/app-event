package com.example.rent.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGold,
    secondary = LightGold,
    tertiary = DarkGold,
    background = BgPrimary,
    surface = BgSecondary,
    surfaceVariant = BgSecondary,
    onPrimary = BgPrimary,
    onSecondary = BgPrimary,
    onBackground = androidx.compose.ui.graphics.Color.White,
    onSurface = androidx.compose.ui.graphics.Color.White,
    onSurfaceVariant = androidx.compose.ui.graphics.Color.White,
    outline = GlassBorder,
    outlineVariant = GlassBorder
)

@Composable
fun RentTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}