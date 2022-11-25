package io.musicorum.mobile.components

import android.text.format.DateUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import io.musicorum.mobile.LocalNavigation
import io.musicorum.mobile.R
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.serialization.NavigationTrack
import io.musicorum.mobile.serialization.Track
import io.musicorum.mobile.ui.theme.AlmostBlack
import io.musicorum.mobile.ui.theme.Typography
import io.musicorum.mobile.viewmodels.TrackRowViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackItem(
    track: Track,
    favoriteIcon: Boolean = true,
    showTimespan: Boolean = false,
    trackRowViewModel: TrackRowViewModel = viewModel()
) {
    val partialTrack = NavigationTrack(track.name, track.artist.name)
    val dest = Json.encodeToString(partialTrack)
    val listColors = ListItemDefaults.colors(
        containerColor = AlmostBlack
    )
    val ctx = LocalContext.current
    val nav = LocalNavigation.current!!


    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { nav.navigate("track/$dest") },
        headlineText = {
            Text(
                text = track.name,
                style = Typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        colors = listColors,
        supportingText = {
            Text(
                text = track.artist.name,
                style = Typography.bodyMedium
            )
        },
        leadingContent = {
            AsyncImage(
                model = defaultImageRequestBuilder(url = track.bestImageUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .aspectRatio(1f)
            )
        },
        trailingContent = {
            Column {
                if (favoriteIcon) {
                    val loved = remember { mutableStateOf(track.loved) }
                    IconButton(onClick = {
                        loved.value = !loved.value
                        trackRowViewModel.updateFavoritePreference(track, ctx)
                    }) {
                        if (loved.value) {
                            Icon(Icons.Rounded.Favorite, contentDescription = null)
                        } else {
                            Icon(Icons.Rounded.FavoriteBorder, contentDescription = null)
                        }
                    }
                }
                if (showTimespan) {
                    val txt = if (track.attributes?.nowPlaying.toBoolean()) {
                        stringResource(id = R.string.scrobbling_now)
                    } else {
                        DateUtils.getRelativeTimeSpanString(
                            track.date!!.uts.toLong() * 1000,
                            System.currentTimeMillis(),
                            DateUtils.SECOND_IN_MILLIS
                        ).toString()
                    }
                    Text(text = txt, style = Typography.labelMedium)
                }
            }
        }
    )
}