package com.musicorumapp.mobile.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val AppMaterialIcons = Icons.Rounded

private val ColorPalette = darkColors(
    primary = MostlyRed,
    primaryVariant = MostlyRed,
    secondary = LighterRed,
    background = KindaBlack,
    surface = AlmostBlack,
    onPrimary = Color.White,
    onBackground = Color.White,


    /* Other default colors to override
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun MusicorumTheme(content: @Composable() () -> Unit) {
    MaterialTheme(
        colors = ColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content,

    )
}