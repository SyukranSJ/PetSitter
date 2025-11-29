package com.caretail.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CareLight = lightColorScheme(
    primary = Color(0xFF4F46E5),
    secondary = Color(0xFF22C55E),
    background = Color(0xFFF7F7FB),
    surface = Color(0xFFFFFFFF),
)

private val CareDark = darkColorScheme(
    primary = Color(0xFF818CF8),
    secondary = Color(0xFF34D399),
)

@Composable
fun CareTailTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) CareDark else CareLight,
        typography = MaterialTheme.typography,
        content = content
    )
}