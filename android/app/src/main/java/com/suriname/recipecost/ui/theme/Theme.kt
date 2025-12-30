package com.suriname.recipecost.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Suriname-inspired colors
val SurinameGreen = Color(0xFF2E7D32)
val SurinameGreenDark = Color(0xFF1B5E20)
val SurinameGold = Color(0xFFFF9800)
val SurinameRed = Color(0xFFDC3545)

private val LightColorScheme = lightColorScheme(
    primary = SurinameGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF002204),
    secondary = SurinameGold,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFFFE0B2),
    onSecondaryContainer = Color(0xFF331B00),
    tertiary = Color(0xFF00796B),
    onTertiary = Color.White,
    error = SurinameRed,
    onError = Color.White,
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),
    onPrimary = Color(0xFF003A09),
    primaryContainer = SurinameGreen,
    onPrimaryContainer = Color(0xFFC8E6C9),
    secondary = Color(0xFFFFB74D),
    onSecondary = Color(0xFF331B00),
    secondaryContainer = Color(0xFF4D3000),
    onSecondaryContainer = Color(0xFFFFE0B2),
    tertiary = Color(0xFF4DB6AC),
    onTertiary = Color(0xFF00251E),
    error = Color(0xFFCF6679),
    onError = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF2D2D2D),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99)
)

@Composable
fun RecipeCostTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
