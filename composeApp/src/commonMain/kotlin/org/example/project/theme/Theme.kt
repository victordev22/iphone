package org.example.project.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = org.example.project.theme.Purple80,
    secondary = org.example.project.theme.PurpleGrey80,
    tertiary = org.example.project.theme.Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = org.example.project.theme.Purple40,
    secondary = org.example.project.theme.PurpleGrey40,
    tertiary = org.example.project.theme.Pink40
)

@Composable
fun ControlHTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
