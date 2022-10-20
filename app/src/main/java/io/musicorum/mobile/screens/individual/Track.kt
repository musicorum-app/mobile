package io.musicorum.mobile.screens.individual

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.palette.graphics.Palette
import coil.compose.rememberAsyncImagePainter
import io.musicorum.mobile.components.Statistic
import io.musicorum.mobile.components.TagList
import io.musicorum.mobile.serialization.NavigationTrack
import io.musicorum.mobile.ui.theme.AlmostBlack
import io.musicorum.mobile.ui.theme.Body1
import io.musicorum.mobile.ui.theme.Heading2
import io.musicorum.mobile.utils.createPalette
import io.musicorum.mobile.utils.getBitmap
import io.musicorum.mobile.viewmodels.HomeViewModel
import io.musicorum.mobile.viewmodels.TrackViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


@Composable
fun Track(
    trackData: String?,
    trackViewModel: TrackViewModel = viewModel(),
    homeViewModel: HomeViewModel
) {
    if (trackData == null) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Something went wrong", textAlign = TextAlign.Center)
        }
    } else {
        val partialTrack = Json.decodeFromString<NavigationTrack>(trackData)
        val track = trackViewModel.track.observeAsState()
        val ctx = LocalContext.current
        var coverPalette: Palette? by remember { mutableStateOf(null) }
        var paletteReady by remember { mutableStateOf(false) }
        val similarTracks = trackViewModel.similar.observeAsState()
        LaunchedEffect(key1 = track.value) {
            if (track.value == null) {
                trackViewModel.fetchTrack(
                    partialTrack.trackName,
                    partialTrack.trackArtist,
                    homeViewModel.user.value!!.user.name,
                    null
                )
            } else {
                trackViewModel.fetchSimilar(track.value!!, 5, null)

                launch {
                    if (!track.value!!.album?.images.isNullOrEmpty()) {
                        val bmp = getBitmap(track.value!!.album!!.images!![0].url, ctx)
                        coverPalette = createPalette(bmp)
                        paletteReady = true
                    } else {
                        paletteReady = true
                    }
                }
            }
        }
        if (track.value == null) {
            Row(
                Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val t = track.value!!

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AlmostBlack),
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    rememberAsyncImagePainter(t.album?.images?.get(3)?.url),
                    contentDescription = "TODO",
                    Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .size(300.dp)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = t.name,
                    softWrap = true,
                    style = Heading2,
                    modifier = Modifier.padding(horizontal = 55.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = t.artist.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = Body1,
                    modifier = Modifier.alpha(0.55f)
                )
                Divider(Modifier.padding(vertical = 18.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Statistic(number = t.listeners, label = "Listeners")
                    Statistic(number = t.playCount, label = "Scrobbles")
                    Statistic(number = t.userPlayCount, label = "Your scrobbles")
                }

                Spacer(modifier = Modifier.height(20.dp))
                if (t.topTags != null) {
                    TagList(tags = t.topTags.tags, coverPalette, !paletteReady)
                }
                if (similarTracks.value != null) {
                    Row {
                        Text("Similar Tracks")
                    }
                    similarTracks.value!!.similarTracks.tracks.forEach {
                        Row {
                            Text(it.name)
                        }
                    }
                }
            }
        }


    }
}