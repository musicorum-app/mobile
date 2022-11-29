package io.musicorum.mobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColorPalette = darkColorScheme(
    primary = MostlyRed,
    secondary = MostlyRed,
    tertiary = LighterRed,
    background = KindaBlack,
    surface = KindaBlack,
    onPrimary = Color.White,
    onBackground = Color.White,
)

@Composable
fun MusicorumMobileTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = Typography,
        content = content,
        colorScheme = ColorPalette
    )
}