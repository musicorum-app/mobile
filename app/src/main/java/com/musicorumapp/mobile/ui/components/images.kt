package com.musicorumapp.mobile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.res.painterResource
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.musicorumapp.mobile.R
import com.musicorumapp.mobile.api.models.LastfmImages
import com.musicorumapp.mobile.ui.theme.SkeletonPrimaryColor

@Composable
fun NetworkImageComponent(
    contentDescription: String,
    modifier: Modifier = Modifier,
    painter: ImagePainter
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
    val painter = rememberImagePainter(
        url,
    )

    NetworkImageComponent(
        contentDescription = contentDescription,
        painter = painter,
        modifier = modifier
    )
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun LastfmImageComponent(
    images: LastfmImages?,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val painterID = images?.type?.asDrawableSource() ?: R.drawable.ic_placeholder_user

    val painter = rememberImagePainter(
        images?.bestImage
        )

    Box(
        modifier = modifier
    ) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
        )

        when (painter.state) {
            is ImagePainter.State.Success -> {

            }
            else -> {
                Image(painter = painterResource(id = painterID), contentDescription = contentDescription)
            }
        }
    }
}