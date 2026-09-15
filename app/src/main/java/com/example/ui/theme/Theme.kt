package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryLight,
    onPrimary = Slate950,
    primaryContainer = PrimaryDark,
    onPrimaryContainer = Color.White,
    secondary = EmeraldSuccess,
    onSecondary = Slate950,
    secondaryContainer = EmeraldDark,
    onSecondaryContainer = Color.White,
    tertiary = AmberAccent,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = Slate100,
    onSurface = Slate100,
    onSurfaceVariant = Slate300,
    outline = DarkCardBorder,
    error = CrimsonDeduction
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryIndigo,
    onPrimary = Color.White,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = EmeraldSuccess,
    onSecondary = Color.White,
    secondaryContainer = EmeraldContainer,
    onSecondaryContainer = OnEmeraldContainer,
    tertiary = AmberAccent,
    background = Slate50,
    surface = Color.White,
    surfaceVariant = Slate100,
    onBackground = Slate900,
    onSurface = Slate900,
    onSurfaceVariant = Slate600,
    outline = Slate200,
    error = CrimsonDeduction
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeColor: String = "INDIGO",
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val (primaryLight, primaryDark, primaryContainer, onPrimaryContainer) = when (themeColor) {
        "EMERALD" -> listOf(ThemeEmerald, ThemeEmeraldDark, ThemeEmeraldContainer, OnThemeEmeraldContainer)
        "AMBER" -> listOf(ThemeAmber, ThemeAmberDark, ThemeAmberContainer, OnThemeAmberContainer)
        "TEAL" -> listOf(ThemeTeal, ThemeTealDark, ThemeTealContainer, OnThemeTealContainer)
        else -> listOf(PrimaryIndigo, PrimaryDark, PrimaryContainer, OnPrimaryContainer)
    }

    val activeLightColorScheme = LightColorScheme.copy(
        primary = primaryLight,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer
    )

    val activeDarkColorScheme = DarkColorScheme.copy(
        primary = when (themeColor) {
            "EMERALD" -> ThemeEmeraldLight
            "AMBER" -> ThemeAmberLight
            "TEAL" -> ThemeTealLight
            else -> PrimaryLight
        },
        primaryContainer = primaryDark
    )

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> activeDarkColorScheme
        else -> activeLightColorScheme
    }

    // Wrap in RTL Layout Direction for Persian language support
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}

