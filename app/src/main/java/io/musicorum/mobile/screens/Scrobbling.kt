package io.musicorum.mobile.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring.StiffnessLow
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.musicorum.mobile.LocalUser
import io.musicorum.mobile.R
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.components.CenteredLoadingSpinner
import io.musicorum.mobile.components.TrackItem
import io.musicorum.mobile.ktor.endpoints.TrackEndpoint
import io.musicorum.mobile.serialization.Track
import io.musicorum.mobile.ui.theme.AlmostBlack
import io.musicorum.mobile.ui.theme.LabelMedium2
import io.musicorum.mobile.ui.theme.LightGray
import io.musicorum.mobile.ui.theme.Typography
import io.musicorum.mobile.utils.*
import io.musicorum.mobile.viewmodels.ScrobblingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun Scrobbling(scrobblingViewModel: ScrobblingViewModel = hiltViewModel()) {
    val user = LocalUser.current!!
    val state = rememberLazyListState()
    val firstItemOffset = remember { derivedStateOf { state.firstVisibleItemScrollOffset } }
    val firstItemIndex = remember { derivedStateOf { state.firstVisibleItemIndex } }
    val interpolated = Utils.interpolateValues(firstItemOffset.value.toFloat(), 0f, 100f, 122f, 46f)
    val value = interpolated.coerceIn(46f..122f)
    val size = animateFloatAsState(if (firstItemIndex.value == 0) value else 46f)

    LaunchedEffect(key1 = scrobblingViewModel.recentScrobbles.value) {
        if (scrobblingViewModel.recentScrobbles.value == null) {
            scrobblingViewModel.updateScrobbles(user.user.name)
        }
    }

    if (scrobblingViewModel.recentScrobbles.value == null) {
        CenteredLoadingSpinner()
    } else {
        Column(
            modifier = Modifier
                .background(AlmostBlack)
                .padding(top = 60.dp, start = 14.dp, end = 14.dp)
                .fillMaxSize()
        ) {
            Column {
                NowPlayingCard(
                    track = scrobblingViewModel.recentScrobbles.value!!.recentTracks.tracks[0],
                    size.value.dp,
                )
            }
            TrackList(vm = scrobblingViewModel, state = state)
        }
    }
}

@Composable
fun NowPlayingCard(track: Track, size: Dp) {
    val isPlaying = track.attributes?.nowPlaying == "true"
    val vBias = Utils.interpolateValues(size.value, 122f, 46f, 1f, 0f)
    val iconAlignment = BiasAlignment(1f, vBias)
    val loved = remember { mutableStateOf(track.loved) }
    val ctx = LocalContext.current
    var palette by remember { mutableStateOf<Palette?>(null) }
    LaunchedEffect(track) {
        launch {
            val bmp = getBitmap(track.bestImageUrl, ctx)
            val p = createPalette(bmp)
            palette = p
        }
    }
    val vibrantColor =
        Color(palette?.getDarkVibrantColor(LightGray.toArgb()) ?: LightGray.toArgb())
    val darken = darkenColor(vibrantColor.toArgb(), 0.5f)

    val vibrant = animateColorAsState(
        targetValue = vibrantColor,
        animationSpec = spring(stiffness = StiffnessLow)
    )
    val dark =
        animateColorAsState(targetValue = darken, animationSpec = spring(stiffness = StiffnessLow))

    val brush = if (track.attributes?.nowPlaying == "true") {
        Brush.horizontalGradient(
            colors = listOf(vibrant.value, dark.value)
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(LightGray, darkenColor(LightGray.toArgb(), 0.5f))
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(brush)
            .statusBarsPadding()
    ) {
        if (isPlaying) {
            IconButton(
                onClick = {
                    CoroutineScope(Dispatchers.Default).launch {
                        TrackEndpoint.updateFavoritePreference(track, loved.value, ctx)
                        loved.value = !loved.value
                    }
                },
                modifier = Modifier
                    .align(iconAlignment)
                    .animateContentSize()
            ) {
                if (loved.value) {
                    Icon(Icons.Rounded.Favorite, null)
                } else {
                    Icon(Icons.Rounded.FavoriteBorder, null)
                }
            }
        }
        Column(
            modifier = Modifier.padding(start = 14.dp, top = 14.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            if (isPlaying) {
                Row(
                    modifier = Modifier
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Rive.AnimationFor(
                        id = R.raw.nowplaying,
                        _alpha = 0.55f,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = "NOW PLAYING", style = LabelMedium2)
                }
            }

            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .padding(bottom = 15.dp)
            ) {
                if (isPlaying) {
                    AsyncImage(
                        model = defaultImageRequestBuilder(url = track.bestImageUrl),
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
                        text = "Nothing playing",
                        style = Typography.titleMedium,
                        modifier = Modifier.height(size)
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
private fun TrackList(vm: ScrobblingViewModel, state: LazyListState) {
    val user = LocalUser.current
    val refreshing = rememberSwipeRefreshState(isRefreshing = vm.refreshing.value)

    SwipeRefresh(state = refreshing, onRefresh = { vm.updateScrobbles(user!!.user.name) }) {
        Column(modifier = Modifier.fillMaxHeight()) {
            val list =
                if (vm.recentScrobbles.value!!.recentTracks.tracks.first().attributes?.nowPlaying == "true") {
                    vm.recentScrobbles.value!!.recentTracks.tracks.drop(1)
                } else {
                    vm.recentScrobbles.value!!.recentTracks.tracks
                }

            LazyColumn(state = state) {
                items(list) { track ->
                    TrackItem(
                        track = track,
                        favoriteIcon = false,
                        showTimespan = true
                    )
                }
            }
        }
    }
}
