package com.example.satuhati.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SatuHatiColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = PrimarySoftBlue,
    onPrimaryContainer = PrimaryDarkNavy,
    secondary = PrimaryDarkNavy,
    onSecondary = Color.White,
    tertiary = StatusSuccess,
    onTertiary = Color.White,
    background = ScreenBackground,
    onBackground = TextDark,
    surface = ScreenBackground,
    onSurface = TextDark,
    surfaceVariant = InputBackground,
    onSurfaceVariant = TextMuted,
    outline = BorderNeutral
)

@Composable
fun SatuHatiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = SatuHatiColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = Color.Transparent.toArgb()
                WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
