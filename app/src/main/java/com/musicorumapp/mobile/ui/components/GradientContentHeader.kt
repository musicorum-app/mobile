package com.musicorumapp.mobile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.imageloading.LoadPainter
import com.google.accompanist.insets.statusBarsPadding
import com.musicorumapp.mobile.ui.theme.PaddingSpacing


private val backgroundHeight = 520.dp

@Composable
fun GradientContentHeader(
    painter: LoadPainter<Any>,
    title: String,
    ) {

    val imageSize = LocalConfiguration.current.screenWidthDp.dp - (PaddingSpacing.HorizontalMainPadding * 2)

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
            .padding(top = 260.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = PaddingSpacing.HorizontalMainPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painter,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(imageSize, imageSize).clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(title,
                style = MaterialTheme.typography.h5,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}