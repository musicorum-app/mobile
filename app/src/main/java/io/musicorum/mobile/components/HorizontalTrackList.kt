package io.musicorum.mobile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder
import io.musicorum.mobile.serialization.Track
import io.musicorum.mobile.ui.theme.SkeletonPrimaryColor

@Composable
fun HorizontalTrackList(tracks: List<Track>?, labelType: LabelType, nav: NavHostController) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .padding(start = 20.dp)
            .placeholder(
                tracks.isNullOrEmpty(),
                color = SkeletonPrimaryColor,
                highlight = PlaceholderHighlight.shimmer()
            )
    ) {
        if (!tracks.isNullOrEmpty()) {
            items(tracks) { track ->
                TrackCard(track = track, labelType, nav)
            }
        }
    }
}