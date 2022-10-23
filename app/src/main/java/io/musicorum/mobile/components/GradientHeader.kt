package io.musicorum.mobile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.musicorum.mobile.ui.theme.AlmostBlack

private val headerHeight = 400.dp

@Composable
fun GradientHeader(imagePainter: Painter, cover: Painter) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = imagePainter,
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            AlmostBlack.copy(alpha = 0.20f),
                            AlmostBlack.copy(alpha = 0.50f),
                            AlmostBlack
                        )
                    )
                )
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Image(
                cover,
                contentDescription = "TODO",
                Modifier
                    .padding(top = 200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .size(300.dp)

            )
        }
    }
}
