package com.fenixgames.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF006B5F),
    onPrimary = Color.White,
    secondary = Color(0xFF8B5E00),
    onSecondary = Color.White,
    background = Color(0xFFF7FAF8),
    onBackground = Color(0xFF17211F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF17211F)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF6ED7C8),
    onPrimary = Color(0xFF003D35),
    secondary = Color(0xFFFFC35A),
    onSecondary = Color(0xFF432C00),
    background = Color(0xFF111816),
    onBackground = Color(0xFFE1E8E5),
    surface = Color(0xFF18211F),
    onSurface = Color(0xFFE1E8E5)
)

@Composable
fun FenixGamesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}

