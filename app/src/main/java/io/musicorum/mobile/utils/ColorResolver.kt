package io.musicorum.mobile.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult

suspend fun getBitmap(url: String, context: Context): Bitmap {
    val loading = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(url)
        .bitmapConfig(Bitmap.Config.RGBA_F16)
        .build()
    val result = (loading.execute(request) as SuccessResult).drawable
    return (result as BitmapDrawable).bitmap
}

fun createPalette(bmp: Bitmap) = Palette.from(bmp).generate()

fun darkenColor(color: Int, factor: Float): Color {
     val darken = ColorUtils.blendARGB(color, Color.Black.toArgb(), factor)
    return Color(darken)
}