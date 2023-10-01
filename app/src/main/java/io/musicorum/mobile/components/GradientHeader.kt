package io.musicorum.mobile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.musicorum.mobile.coil.PlaceholderType
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.ui.theme.KindaBlack

val HEADER_HEIGHT = 400.dp

@Composable
fun GradientHeader(
    backgroundUrl: String?,
    coverUrl: String?,
    shape: Shape,
    placeholderType: PlaceholderType,
    showCover: Boolean = true
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        AsyncImage(
            model = backgroundUrl,
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(HEADER_HEIGHT)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(HEADER_HEIGHT)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            KindaBlack.copy(alpha = 0.20f),
                            KindaBlack.copy(alpha = 0.50f),
                            KindaBlack
                        )
                    )
                )
        )
        if (showCover) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                AsyncImage(
                    model = defaultImageRequestBuilder(url = coverUrl, placeholderType),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(top = 200.dp)
                        .shadow(elevation = 20.dp, shape = shape, spotColor = Color.Black)
                        .clip(shape)
                        .size(300.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
