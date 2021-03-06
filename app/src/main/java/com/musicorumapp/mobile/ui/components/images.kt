package com.musicorumapp.mobile.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.res.painterResource
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState
import com.google.accompanist.imageloading.LoadPainter
import com.musicorumapp.mobile.R
import com.musicorumapp.mobile.api.models.LastfmImages
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
            contentDescription = contentDescription,

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

@Composable
fun LastfmImageComponent(
    images: LastfmImages?,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val painterID = images?.type?.asDrawableSource() ?: R.drawable.ic_placeholder_user

    val painter = rememberCoilPainter(
        images?.bestImage,
        fadeIn = true,
        previewPlaceholder = painterID

        )

    Box(
        modifier = modifier
    ) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
        )

        when (painter.loadState) {
            is ImageLoadState.Success -> {

            }
            else -> {
                Image(painter = painterResource(id = painterID), contentDescription = contentDescription)
            }
        }
    }
}