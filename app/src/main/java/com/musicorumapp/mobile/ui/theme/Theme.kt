package com.musicorumapp.mobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val AppMaterialIcons = Icons.Rounded

private val ColorPalette = darkColorScheme(

    primary = MostlyRed,
    secondary = MostlyRed,
    tertiary = LighterRed,
    background = KindaBlack,
    surface = AlmostBlack,
    onPrimary = Color.White,
    onBackground = Color.White
)

@Composable
fun MusicorumTheme(content: @Composable() () -> Unit) {
    MaterialTheme(
        typography = Typography,
        content = content,
        colorScheme = ColorPalette
    )
}