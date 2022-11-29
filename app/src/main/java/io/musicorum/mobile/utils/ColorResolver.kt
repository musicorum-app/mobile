package io.musicorum.mobile.utils

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import com.github.ajalt.colormath.extensions.android.composecolor.toColormathColor
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import io.musicorum.mobile.R

suspend fun getBitmap(url: String?, context: Context): Bitmap {
    val loading = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(url)
        .bitmapConfig(Bitmap.Config.RGBA_F16)
        .error(R.drawable.track_placeholder)
        .build()
    val result = loading.execute(request)
    return result.drawable?.toBitmap()!!
}

fun createPalette(bmp: Bitmap) = Palette.from(bmp).generate()

fun darkenColor(color: Int, factor: Float): Color {
    val darken = ColorUtils.blendARGB(color, Color.Black.toArgb(), factor)
    return Color(darken)
}

fun getDarkenGradient (color: Color): List<Color> {
    val hsl = color.toColormathColor().toHSL()

    val color1 = hsl.copy(l = 0.35f).toComposeColor()
    val color2 = hsl.copy(l = 0.15f).toComposeColor()

    return listOf(color1, color2)
}