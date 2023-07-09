package io.musicorum.mobile.components

import android.text.format.DateUtils
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.ktor.http.encodeURLPathPart
import io.musicorum.mobile.LocalNavigation
import io.musicorum.mobile.R
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.serialization.NavigationTrack
import io.musicorum.mobile.serialization.entities.Track
import io.musicorum.mobile.ui.theme.KindaBlack
import io.musicorum.mobile.ui.theme.MostlyRed
import io.musicorum.mobile.ui.theme.Typography
import io.musicorum.mobile.utils.Rive
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrackCard(track: Track, labelType: LabelType) {
    val interactionSource = remember { MutableInteractionSource() }
    val nav = LocalNavigation.current
    val navTrack = NavigationTrack(track.name.encodeURLPathPart(), track.artist.name)
    val dest = Json.encodeToString(navTrack)
    val showTrackSheet = remember { mutableStateOf(false) }

    if (showTrackSheet.value) {
        TrackSheet(track = track, show = showTrackSheet)
    }

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                enabled = true,
                indication = null,
                interactionSource = interactionSource,
                onClick = { nav?.navigate("track/$dest") },
                onLongClick = { showTrackSheet.value = true }
            )
    ) {
        Box(modifier = Modifier.width(120.dp)) {
            AsyncImage(
                model = defaultImageRequestBuilder(url = track.bestImageUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(125.dp)
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(6.dp))
                    .aspectRatio(1f)
                    .indication(interactionSource, LocalIndication.current)
            )
            if (track.attributes?.nowPlaying == "true") {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(40.dp)
                        .offset(5.dp, 10.dp)
                        .clip(CircleShape)
                        .background(color = KindaBlack, shape = CircleShape)
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(MostlyRed),
                    contentAlignment = Alignment.Center
                ) {
                    Rive.AnimationFor(
                        id = R.raw.nowplaying,
                        modifier = Modifier
                            .size(20.dp)
                    )
                }
            }
        }
        Text(
            text = track.name,
            textAlign = TextAlign.Start,
            style = Typography.bodyLarge,
            modifier = Modifier
                .width(120.dp)
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
            Text(text, modifier = Modifier.alpha(0.55f), style = Typography.bodyLarge)
        } else if (labelType == LabelType.ARTIST_NAME) {
            Text(
                track.artist.name,
                modifier = Modifier
                    .alpha(0.55f)
                    .width(120.dp),
                style = Typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,

                )
        }
    }
}

