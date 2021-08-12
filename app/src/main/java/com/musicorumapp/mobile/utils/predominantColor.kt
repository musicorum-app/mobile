package com.musicorumapp.mobile.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.Coil
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.github.ajalt.colormath.HSL
import com.github.ajalt.colormath.RGB
import com.musicorumapp.mobile.Constants
import com.musicorumapp.mobile.ui.theme.SkeletonPrimaryColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun rememberPredominantColor(
    context: Context = LocalContext.current,
    defaultColor: Color = SkeletonPrimaryColor,
    defaultOnColor: Color = MaterialTheme.colors.onSurface,
    colorFinder: (palette: Palette) -> Palette.Swatch? = { it.swatches.first() },
    colorResolver: (color: Color) -> Color = { it }
): PredominantColorState = remember {
    PredominantColorState(context, defaultColor, defaultOnColor, colorFinder, colorResolver)
}


@Stable
class PredominantColorState(
    private val context: Context,
    private val defaultColor: Color,
    private val defaultOnColor: Color,
    private val colorFinder: (palette: Palette) -> Palette.Swatch?,
    private val colorResolver: (color: Color) -> Color
) {

    var color by mutableStateOf(defaultColor)
        private set

    var onColor by mutableStateOf(defaultOnColor)
        private set

    suspend fun resolveColorsFromURL(url: String) {
        withContext(Dispatchers.Default) {
            val palette = fetchImageColors(
                context,
                url
            )

            if (palette != null) {
                val swatch = colorFinder(palette)

                color = if (swatch != null) colorResolver(Color(swatch.rgb)) else defaultColor
                onColor = getTextColorFromContrastOf(color)
            }
        }
    }

    suspend fun resolveColorsFromBitmap(bitmap: Bitmap) {
        withContext(Dispatchers.Default) {
            val palette = fetchImageColorsFromBitmap(bitmap)

            if (palette != null) {
                val swatch = colorFinder(palette)

                color = if (swatch != null) colorResolver(Color(swatch.rgb)) else defaultColor
                onColor = getTextColorFromContrastOf(color)
            }
        }
    }
}

private suspend fun fetchImageColors(
    context: Context,
    imageUrl: String
): Palette? {
    val req = ImageRequest.Builder(context)
        .data(imageUrl)
        .size(200)
        .allowHardware(false)
        .build()

    val bitmap = when (
        val result = Coil.execute(req)
    ) {
        is SuccessResult -> result.drawable.toBitmap()
        else -> null
    }

    return bitmap?.let { fetchImageColorsFromBitmap(it) }
}

private fun fetchImageColorsFromBitmap(bitmap: Bitmap): Palette? {
    return Palette.Builder(bitmap)
        .resizeBitmapArea(0)
//            .clearFilters()
        .maximumColorCount(8)
        .generate()
}

private fun composeColorToAndroidColorInt(color: Color): Int {
    return android.graphics.Color.argb(
        255,
        resolveVector(color.red),
        resolveVector(color.green),
        resolveVector(color.blue)
    )
}

fun getTextColorFromContrastOf(a: Color): Color {
    val background = composeColorToAndroidColorInt(a)

    val contrast = ColorUtils.calculateContrast(android.graphics.Color.WHITE, background)
    Log.i(Constants.LOG_TAG, "Contrast value: $contrast")

    return if (contrast < 3) Color.Black else Color.White
}

fun calculateColorContrast(a: Color, b: Color): Double {
    return ColorUtils.calculateContrast(
        composeColorToAndroidColorInt(a),
        composeColorToAndroidColorInt(b)
    )
}

fun gradientBackgroundColorResolver(color: Color): Color {
    var hsl =
        RGB(resolveVector(color.red), resolveVector(color.green), resolveVector(color.blue)).toHSL()

    val l = if (hsl.l > 50) 50 else hsl.l

    hsl = HSL(hsl.h, hsl.s, l)

    val rgb = hsl.toRGB()

    Log.i(Constants.LOG_TAG, hsl.toString())

    return Color(
        red = rgb.r,
        green = rgb.g,
        blue = rgb.b
    )
}

private fun resolveVector(f: Float): Int {
    return (f * 255).toInt()
}

fun darkerColor(color: Color, qnt: Int = 10): Color {
    var hsl =
        RGB(resolveVector(color.red), resolveVector(color.green), resolveVector(color.blue)).toHSL()

    val l = hsl.l - qnt

    hsl = HSL(hsl.h, hsl.s, l.coerceIn(0, 100))

    val rgb = hsl.toRGB()

    return Color(
        red = rgb.r,
        green = rgb.g,
        blue = rgb.b
    )
}