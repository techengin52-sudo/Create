package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CinematicDarkColorScheme = darkColorScheme(
    primary = CinematicAmber,
    onPrimary = CinematicBlack,
    primaryContainer = CinematicDarkSurface,
    onPrimaryContainer = CinematicGold,
    secondary = CinematicNeonCyan,
    onSecondary = CinematicBlack,
    secondaryContainer = CinematicCardSurface,
    onSecondaryContainer = CinematicNeonCyan,
    tertiary = CinematicViolet,
    onTertiary = TextPrimary,
    background = CinematicBlack,
    onBackground = TextPrimary,
    surface = CinematicDarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = CinematicCardSurface,
    onSurfaceVariant = TextSecondary,
    outline = CinematicCardBorder,
    error = ErrorRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force cinematic dark UI as requested
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = CinematicDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = CinematicBlack.toArgb()
                window.navigationBarColor = CinematicBlack.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
