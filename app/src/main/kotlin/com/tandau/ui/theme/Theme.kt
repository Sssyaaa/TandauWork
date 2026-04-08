package com.tandau.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Tandau Palette
val TandauYellow = Color(0xFFFFE600)
val TandauBlack = Color(0xFF121212)
val TandauDarkGrey = Color(0xFF1E1E1E)
val TandauLightGrey = Color(0xFF2C2C2C)
val TandauWhite = Color(0xFFFFFFFF)

private val DarkColorScheme = darkColorScheme(
    primary = TandauYellow,
    onPrimary = Color.Black,
    primaryContainer = TandauYellow,
    onPrimaryContainer = Color.Black,
    secondary = TandauYellow,
    onSecondary = Color.Black,
    background = Color.Black,
    onBackground = TandauWhite,
    surface = TandauDarkGrey,
    onSurface = TandauWhite,
    surfaceVariant = TandauLightGrey,
    onSurfaceVariant = TandauWhite.copy(alpha = 0.7f),
    outline = TandauYellow.copy(alpha = 0.5f)
)

// For now, we'll keep LightColorScheme similar or just use Dark as the default for Tandau's brand identity
private val LightColorScheme = lightColorScheme(
    primary = TandauYellow,
    onPrimary = Color.Black,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black
)

@Composable
fun TandauTheme(
    darkTheme: Boolean = true, // Force dark theme for Tandau branding
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Assuming Typography is defined
        content = content
    )
}
