package io.musicorum.mobile.components.skeletons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import io.musicorum.mobile.R
import io.musicorum.mobile.ui.theme.SkeletonPrimaryColor

@Composable
fun GenericListItemSkeleton(visible: Boolean) {
    ListItem(
        headlineContent = {
            Text(
                "headline long text",
                modifier = Modifier.placeholder(
                    visible,
                    SkeletonPrimaryColor,
                    highlight = PlaceholderHighlight.fade()
                )
            )
        },
        leadingContent = {
            Image(
                painter = painterResource(id = R.drawable.track_placeholder),
                contentDescription = null,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .placeholder(
                        visible,
                        SkeletonPrimaryColor,
                        highlight = PlaceholderHighlight.fade()
                    )
            )
        }
    )
}