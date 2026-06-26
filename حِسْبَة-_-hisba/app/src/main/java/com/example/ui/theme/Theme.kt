package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val NeonCyan = Color(0xFF00E5FF)
val NeonPurple = Color(0xFFB537F2)
val NeonPink = Color(0xFFFF2BD6)
val NeonGreen = Color(0xFF00FFA3)
val NeonOrange = Color(0xFFFF9500)

val DarkBackgroundStart = Color(0xFF05060F)
val DarkBackgroundEnd = Color(0xFF0A0D1F)
val SurfaceDark = Color(0x33FFFFFF) // Glass effect
val SurfaceDarkNeon = Color(0x1100E5FF)

val LightBackgroundStart = Color(0xFFE0E5EC)
val LightBackgroundEnd = Color(0xFFF0F5FA)
val SurfaceLight = Color(0x99FFFFFF)

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    secondary = NeonPurple,
    tertiary = NeonPink,
    background = DarkBackgroundStart,
    surface = SurfaceDark,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    error = NeonOrange
)

private val LightColorScheme = lightColorScheme(
    primary = NeonCyan,
    secondary = NeonPurple,
    tertiary = NeonPink,
    background = LightBackgroundStart,
    surface = SurfaceLight,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    error = NeonOrange
)

@Composable
fun HisbaTheme(
    darkTheme: Boolean = true, // Default to dark as requested
    dynamicColor: Boolean = false, // Disable dynamic to keep neon vibes
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
