package io.musicorum.mobile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.musicorum.mobile.R
import io.musicorum.mobile.components.skeletons.GenericCardPlaceholder
import io.musicorum.mobile.serialization.entities.Track
import io.musicorum.mobile.ui.theme.Subtitle1

enum class LabelType {
    DATE, ARTIST_NAME
}

@Composable
fun HorizontalTracksRow(
    tracks: List<Track>?,
    labelType: LabelType,
    errored: Boolean?
) {
    if (errored == true) {
        Text(
            text = stringResource(R.string.empty_tracklist_message),
            style = Subtitle1,
            modifier = Modifier.padding(start = 20.dp)
        )
    } else {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(start = 20.dp)
        ) {
            if (tracks.isNullOrEmpty()) {
                items(4) { _ ->
                    GenericCardPlaceholder(visible = tracks.isNullOrEmpty())
                }
            } else {
                items(tracks) { track ->
                    TrackCard(track = track, labelType)
                }
            }
        }
    }
}
