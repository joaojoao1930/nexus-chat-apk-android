package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.model.AccentColor
import com.example.model.ThemeMode

@Composable
fun NexusChatTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    accent: AccentColor = AccentColor.CYAN,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val primaryColor = when (accent) {
        AccentColor.CYAN -> NexusTeal
        AccentColor.EMERALD -> NexusEmerald
        AccentColor.PURPLE -> NexusPurple
        AccentColor.SUNSET -> NexusSunset
    }

    val secondaryColor = when (accent) {
        AccentColor.CYAN -> NexusCyan
        AccentColor.EMERALD -> NexusTealDark
        AccentColor.PURPLE -> NexusIndigo
        AccentColor.SUNSET -> NexusPink
    }

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = primaryColor,
            onPrimary = Color(0xFF041B20),
            primaryContainer = primaryColor.copy(alpha = 0.2f),
            onPrimaryContainer = primaryColor,
            secondary = secondaryColor,
            onSecondary = Color.White,
            secondaryContainer = secondaryColor.copy(alpha = 0.2f),
            background = DarkBg,
            onBackground = Color(0xFFF0F4F8),
            surface = DarkSurface,
            onSurface = Color(0xFFE2E8F0),
            surfaceVariant = DarkSurfaceVariant,
            onSurfaceVariant = Color(0xFF94A3B8),
            outline = DarkOutline,
            surfaceContainer = DarkCard
        )
    } else {
        lightColorScheme(
            primary = when (accent) {
                AccentColor.CYAN -> NexusTealDark
                AccentColor.EMERALD -> Color(0xFF0F766E)
                AccentColor.PURPLE -> Color(0xFF6B21A8)
                AccentColor.SUNSET -> Color(0xFFC2410C)
            },
            onPrimary = Color.White,
            primaryContainer = primaryColor.copy(alpha = 0.15f),
            onPrimaryContainer = Color(0xFF042F2E),
            secondary = secondaryColor,
            onSecondary = Color.White,
            background = LightBg,
            onBackground = Color(0xFF0F172A),
            surface = LightSurface,
            onSurface = Color(0xFF1E293B),
            surfaceVariant = LightSurfaceVariant,
            onSurfaceVariant = Color(0xFF64748B),
            outline = LightOutline,
            surfaceContainer = Color(0xFFFFFFFF)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    NexusChatTheme(content = content)
}

