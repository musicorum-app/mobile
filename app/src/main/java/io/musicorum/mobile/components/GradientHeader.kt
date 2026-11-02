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

internal object DetailHeaderDefaults {
    val height = 400.dp
    val coverSize = 300.dp
    val coverTopPadding = 200.dp
    const val initialAppBarContentOffset = 700f
}

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
                .height(DetailHeaderDefaults.height)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(DetailHeaderDefaults.height)
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
                        .padding(top = DetailHeaderDefaults.coverTopPadding)
                        .shadow(elevation = 20.dp, shape = shape, spotColor = Color.Black)
                        .clip(shape)
                        .size(DetailHeaderDefaults.coverSize),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
