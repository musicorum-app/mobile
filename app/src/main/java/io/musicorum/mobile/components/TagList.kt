package io.musicorum.mobile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.shimmer
import io.musicorum.mobile.serialization.TagData
import io.musicorum.mobile.ui.theme.SkeletonSecondaryColor
import io.musicorum.mobile.ui.theme.Typography
import io.musicorum.mobile.utils.darkenColor

@Composable
fun TagList(tags: List<TagData>, referencePalette: Palette?, visible: Boolean) {
    val borderColor =
        referencePalette?.getVibrantColor(Color.LightGray.toArgb())?.let { Color(it) }
            ?: Color.LightGray
    val background = darkenColor(borderColor.toArgb(), 0.70f)
    LazyRow(
        Modifier.padding(start = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(tags) { tag ->
            Box(
                modifier = Modifier
                    .border(1.dp, borderColor, RoundedCornerShape(100))
                    .clip(RoundedCornerShape(100))
                    .placeholder(
                        visible,
                        highlight = PlaceholderHighlight.shimmer(SkeletonSecondaryColor)
                    )
                    .background(background)
                    .padding(horizontal = 12.dp, vertical = 1.dp)
            ) {
                Text(
                    text = tag.name,
                    style = Typography.labelLarge,
                    modifier = Modifier.padding(bottom = 1.dp)
                )
            }
        }
    }
}