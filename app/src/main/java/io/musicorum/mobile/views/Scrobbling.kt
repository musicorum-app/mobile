package io.musicorum.mobile.views

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring.StiffnessLow
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.musicorum.mobile.R
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.components.CenteredLoadingSpinner
import io.musicorum.mobile.components.TrackListItem
import io.musicorum.mobile.serialization.entities.Track
import io.musicorum.mobile.ui.theme.EvenLighterGray
import io.musicorum.mobile.ui.theme.KindaBlack
import io.musicorum.mobile.ui.theme.LabelMedium2
import io.musicorum.mobile.ui.theme.Typography
import io.musicorum.mobile.utils.Rive
import io.musicorum.mobile.utils.Utils
import io.musicorum.mobile.utils.createPalette
import io.musicorum.mobile.utils.darkenColor
import io.musicorum.mobile.utils.getBitmap
import io.musicorum.mobile.utils.getDarkenGradient
import io.musicorum.mobile.viewmodels.ScrobblingViewModel
import kotlinx.coroutines.launch

@Composable
fun Scrobbling(vm: ScrobblingViewModel = hiltViewModel()) {
    val state = rememberLazyListState()
    val firstItemOffset = remember { derivedStateOf { state.firstVisibleItemScrollOffset } }
    val firstItemIndex = remember { derivedStateOf { state.firstVisibleItemIndex } }
    // 0: closed; 1: open;
    val interpolated = Utils.interpolateValues(firstItemOffset.value.toFloat(), 0f, 200f, 1f, 0f)
    val clamped = interpolated.coerceIn(0f..1f)
    val value = animateFloatAsState(if (firstItemIndex.value == 0) clamped else 0f, label = "")
    val recentTracks = vm.recentScrobbles.observeAsState(null).value
    val npTrack = vm.nowPlayingTrack.observeAsState(null).value


    if (recentTracks == null) {
        CenteredLoadingSpinner()
    } else {
        Column(
            modifier = Modifier
                .background(KindaBlack)
                .padding(top = 30.dp, start = 20.dp, end = 20.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Scrobbling",
                style = Typography.displaySmall,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            Column {
                NowPlayingCard(
                    track = npTrack,
                    fraction = value.value,
                    vm = vm
                )
            }
            TrackList(vm = vm, state = state)
        }
    }
}

@Composable
fun NowPlayingCard(track: Track?, fraction: Float, vm: ScrobblingViewModel) {
    val isPlaying = track?.attributes?.nowPlaying == "true"
    val iconAlignment = BiasAlignment(1f, fraction)
    val textAlignment = BiasAlignment.Vertical(fraction * -1)
    val size = (44f + fraction * 76f).dp
    val nowPlayingHeight = (fraction * 20f).dp
    val nowPlayingAlpha = fraction * 0.65f
    val padding = (10f + fraction * 4f).dp
    val loved = vm.isTrackLoved.observeAsState(false).value
    val ctx = LocalContext.current
    var palette by remember { mutableStateOf<Palette?>(null) }

    LaunchedEffect(track) {
        launch {
            val bmp = getBitmap(track?.bestImageUrl, ctx)
            val p = createPalette(bmp)
            palette = p
        }
    }
    val vibrantColor =
        Color(palette?.getVibrantColor(EvenLighterGray.toArgb()) ?: EvenLighterGray.toArgb())
    val gradient = getDarkenGradient(vibrantColor)

    val vibrant = animateColorAsState(
        targetValue = gradient[0],
        animationSpec = spring(stiffness = StiffnessLow), label = ""
    )
    val dark =
        animateColorAsState(
            targetValue = gradient[1],
            animationSpec = spring(stiffness = StiffnessLow), label = ""
        )

    val brush = if (track?.attributes?.nowPlaying == "true") {
        Brush.horizontalGradient(
            colors = listOf(vibrant.value, dark.value)
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(EvenLighterGray, darkenColor(EvenLighterGray.toArgb(), 0.5f))
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(brush)
    ) {
        if (isPlaying) {
            IconButton(
                onClick = { vm.updateFavorite(track, loved) },
                modifier = Modifier
                    .align(iconAlignment)
                    .animateContentSize()
            ) {
                if (loved) {
                    Icon(Icons.Rounded.Favorite, null)
                } else {
                    Icon(Icons.Rounded.FavoriteBorder, null)
                }
            }
        }
        Column(
            modifier = Modifier.padding(padding),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            if (isPlaying) {
                Spacer(modifier = Modifier.height(nowPlayingHeight))
            }

            Row(
                verticalAlignment = textAlignment,
            ) {
                if (isPlaying) {
                    AsyncImage(
                        model = defaultImageRequestBuilder(url = track!!.bestImageUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .size(size)
                            .clip(RoundedCornerShape(6.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = track.name)
                        Text(text = track.artist.name, style = Typography.titleMedium)
                    }
                } else {
                    Text(
                        text = stringResource(R.string.nothing_playing),
                        style = Typography.titleMedium,
                        modifier = Modifier.height(size)
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .alpha(nowPlayingAlpha)
                .padding(start = padding, top = padding)
        ) {
            if (isPlaying) {
                Row(
                    modifier = Modifier.padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Rive.AnimationFor(
                        id = R.raw.nowplaying,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = "NOW PLAYING", style = LabelMedium2.copy(color = Color.White))
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
private fun TrackList(vm: ScrobblingViewModel, state: LazyListState) {
    val refreshValue = vm.refreshing.observeAsState(false).value
    val refreshing = rememberSwipeRefreshState(isRefreshing = refreshValue)
    SwipeRefresh(state = refreshing, onRefresh = { vm.updateScrobbles() }) {
        Column(modifier = Modifier.fillMaxHeight()) {
            val list =
                if (vm.recentScrobbles.value!!.firstOrNull()?.attributes?.nowPlaying == "true") {
                    vm.recentScrobbles.value!!.drop(1)
                } else {
                    vm.recentScrobbles.value!!
                }

            LazyColumn(state = state) {
                items(list) { track ->
                    TrackListItem(
                        track = track,
                        favoriteIcon = false,
                        showTimespan = true
                    )
                }
            }
        }
    }
}
