package com.artes.securehup.stepapp.ui.theme

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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// Responsive Dimensions
object Dimensions {
    // Padding & Margins
    val paddingExtraSmall = 4.dp
    val paddingSmall = 8.dp
    val paddingMedium = 16.dp
    val paddingLarge = 24.dp
    val paddingExtraLarge = 32.dp
    val paddingXXLarge = 40.dp
    
    // Card & Component Heights
    val buttonHeight = 56.dp
    val buttonHeightLarge = 60.dp
    val cardHeightSmall = 60.dp
    val cardHeightMedium = 80.dp
    val cardHeightLarge = 120.dp
    val inputFieldHeight = 56.dp
    
    // Corner Radius
    val cornerRadiusSmall = 8.dp
    val cornerRadiusMedium = 16.dp
    val cornerRadiusLarge = 20.dp
    val cornerRadiusXLarge = 30.dp
    
    // Icon Sizes
    val iconSizeSmall = 16.dp
    val iconSizeMedium = 24.dp
    val iconSizeLarge = 32.dp
    val iconSizeXLarge = 48.dp
    val iconSizeXXLarge = 64.dp
    
    // Progress & Divider
    val progressBarHeight = 4.dp
    val dividerHeight = 1.dp
    
    // Logo & App Icon
    val appIconSize = 100.dp
    val logoSize = 80.dp
}

private val DarkColorScheme = darkColorScheme(
    primary = NeonGreen,
    onPrimary = DarkBackground,
    primaryContainer = NeonGreenVariant,
    onPrimaryContainer = DarkBackground,
    secondary = TextSecondary,
    onSecondary = DarkBackground,
    secondaryContainer = DarkCard,
    onSecondaryContainer = TextPrimary,
    tertiary = NeonGreenLight,
    onTertiary = DarkBackground,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkCard,
    onSurfaceVariant = TextSecondary,
    surfaceTint = NeonGreen,
    outline = TextTertiary,
    outlineVariant = DarkCard,
    error = StatusError,
    onError = TextPrimary,
    errorContainer = StatusError,
    onErrorContainer = TextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun StepappTheme(
    darkTheme: Boolean = true, // Default to dark theme
    dynamicColor: Boolean = false, // Disable dynamic color to use our custom theme
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
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}