package com.musicorumapp.mobile.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.LoadPainter
import com.musicorumapp.mobile.ui.theme.SkeletonPrimaryColor

@Composable
fun NetworkImageComponent(
    contentDescription: String,
    modifier: Modifier = Modifier,
    painter: LoadPainter<Any>
) {
    Box(
        modifier = Modifier
            .composed { modifier }
            .background(SkeletonPrimaryColor)
    ) {
        Image(
            painter = painter,
            contentDescription = contentDescription
        )
    }
}

@Composable
fun NetworkImage(
    url: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val painter = rememberCoilPainter(
        url,
        fadeIn = true,
    )

    NetworkImageComponent(
        contentDescription = contentDescription,
        painter = painter,
        modifier = modifier
    )
}