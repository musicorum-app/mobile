package io.musicorum.mobile.components

import android.text.format.DateUtils
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import io.musicorum.mobile.R
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.serialization.NavigationTrack
import io.musicorum.mobile.serialization.Track
import io.musicorum.mobile.ui.theme.Poppins
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val label = TextStyle(
    fontFamily = Poppins,
    fontWeight = FontWeight(500),
    fontSize = 12.sp
)

enum class LabelType {
    DATE, ARTIST_NAME
}

@Composable
fun HorizontalTracksRow(tracks: List<Track>?, labelType: LabelType, nav: NavHostController) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .padding(start = 20.dp)
    ) {
        if (!tracks.isNullOrEmpty()) {
            items(tracks) { track ->
                TrackCard(track = track, labelType, nav)
            }
        }
    }
}

@Composable
fun TrackCard(track: Track, labelType: LabelType, nav: NavHostController) {
    val interactionSource = remember { MutableInteractionSource() }
    val navTrack = NavigationTrack(track.name, track.artist.artistName)
    val dest = Json.encodeToString(navTrack)
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .width(120.dp)
            .clip(RoundedCornerShape(6.dp))
            .clickable(
                enabled = true,
                indication = null,
                interactionSource = interactionSource
            ) { nav.navigate("track/$dest") }
    ) {
        AsyncImage(
            model = defaultImageRequestBuilder(url = track.bestImageUrl),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .aspectRatio(1f)
                .indication(interactionSource, LocalIndication.current)
        )
        Text(
            text = track.name,
            textAlign = TextAlign.Start,
            style = label,
            modifier = Modifier
                .padding(top = 7.dp)
                .indication(interactionSource, null),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (labelType == LabelType.DATE) {
            val text = if (track.attributes?.nowPlaying.toBoolean()) {
                stringResource(R.string.scrobbling_now)
            } else {
                val now = System.currentTimeMillis()
                DateUtils.getRelativeTimeSpanString(
                    track.date!!.uts.toLong() * 1000,
                    now,
                    DateUtils.SECOND_IN_MILLIS
                ).toString()
            }
            Text(text, modifier = Modifier.alpha(0.55f), style = label)
        } else if (labelType == LabelType.ARTIST_NAME) {
            Text(
                track.artist.artistName,
                modifier = Modifier.alpha(0.55f),
                style = label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
