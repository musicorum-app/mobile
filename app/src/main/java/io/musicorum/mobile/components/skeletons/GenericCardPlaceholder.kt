package io.musicorum.mobile.components.skeletons

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.components.label
import io.musicorum.mobile.ui.theme.SkeletonSecondaryColor

@Composable
fun GenericCardPlaceholder(visible: Boolean) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .width(120.dp)
            .clip(RoundedCornerShape(6.dp))

    ) {
        AsyncImage(
            model = defaultImageRequestBuilder(url = ""),
            contentDescription = null,
        )
        Text(
            text = "this is a very long text",
            textAlign = TextAlign.Start,
            style = label,
            modifier = Modifier
                .padding(top = 7.dp)
                .placeholder(
                    visible,
                    SkeletonSecondaryColor,
                    highlight = PlaceholderHighlight.fade()
                ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "shorter",
            textAlign = TextAlign.Start,
            style = label,
            modifier = Modifier
                .padding(top = 7.dp)
                .placeholder(
                    visible,
                    SkeletonSecondaryColor,
                    highlight = PlaceholderHighlight.fade()
                ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}