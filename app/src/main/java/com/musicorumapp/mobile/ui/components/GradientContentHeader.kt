package com.musicorumapp.mobile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.google.accompanist.imageloading.LoadPainter
import com.google.accompanist.insets.statusBarsPadding


private val backgroundHeight = 520.dp

@Composable
fun GradientContentHeader(
    painter: LoadPainter<Any>,

    ) {

    val imageSize = 300.dp

    Image(
        painter = painter,
        contentDescription = "",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(backgroundHeight)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(backgroundHeight)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colors.background.copy(alpha = 0.2f),
                        MaterialTheme.colors.background.copy(alpha = 0.5f),
                        MaterialTheme.colors.background,
                    ),
                )
            )
    )

    Box(
        modifier = Modifier
            .statusBarsPadding()
            .padding(top = 200.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painter,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(imageSize, imageSize)
            )
        }
    }
}