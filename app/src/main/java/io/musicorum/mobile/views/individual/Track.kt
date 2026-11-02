package io.musicorum.mobile.views.individual

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.palette.graphics.Palette
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import io.musicorum.mobile.LocalAnalytics
import io.musicorum.mobile.R
import io.musicorum.mobile.coil.PlaceholderType
import io.musicorum.mobile.components.*
import io.musicorum.mobile.components.skeletons.DetailLoadingSkeleton
import io.musicorum.mobile.serialization.NavigationTrack
import io.musicorum.mobile.ui.theme.*
import io.musicorum.mobile.utils.createPalette
import io.musicorum.mobile.utils.getBitmap
import io.musicorum.mobile.viewmodels.TrackLoadState
import io.musicorum.mobile.viewmodels.TrackViewModel
import kotlinx.serialization.json.Json


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Track(
    trackData: String?,
    trackViewModel: TrackViewModel = hiltViewModel()
) {
    val analytics = LocalAnalytics.current!!
    LaunchedEffect(Unit) {
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, "track")
        }
    }
    if (trackData == null) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(KindaBlack), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Something went wrong", textAlign = TextAlign.Center)
        }
    } else {
        val partialTrack = Json.decodeFromString<NavigationTrack>(trackData)
        val track = trackViewModel.track.observeAsState().value
        val ctx = LocalContext.current
        var coverPalette: Palette? by remember { mutableStateOf(null) }
        var paletteReady by remember { mutableStateOf(false) }
        val similarTracks = trackViewModel.similar.observeAsState().value
        val loadState = trackViewModel.loadState.observeAsState(TrackLoadState.LOADING).value

        LaunchedEffect(partialTrack.trackName, partialTrack.trackArtist) {
            trackViewModel.fetchTrack(
                partialTrack.trackName,
                partialTrack.trackArtist,
                null
            )
        }
        LaunchedEffect(track?.name, track?.artist?.name) {
            track?.let {
                trackViewModel.fetchSimilar(it, 5, null)
            }
        }
        val albumImageUrl = track?.album?.bestImageUrl
        LaunchedEffect(albumImageUrl) {
            coverPalette = null
            paletteReady = false
            if (!albumImageUrl.isNullOrBlank()) {
                val bitmap = getBitmap(albumImageUrl, ctx)
                coverPalette = createPalette(bitmap)
            }
            paletteReady = true
        }
        if (loadState == TrackLoadState.ERROR) {
            TrackLoadError {
                trackViewModel.fetchTrack(
                    partialTrack.trackName,
                    partialTrack.trackArtist,
                    null
                )
            }
        } else if (track == null) {
            DetailLoadingSkeleton(
                coverShape = RoundedCornerShape(6.dp),
                showSubtitle = true,
                showContext = true
            )
        } else {
            val screenScrollState = rememberScrollState()
            val appBarState = rememberTopAppBarState(
                initialContentOffset = DetailHeaderDefaults.initialAppBarContentOffset
            )
            val appBarBehavior =
                TopAppBarDefaults.pinnedScrollBehavior(state = appBarState)
            val loved = remember { mutableStateOf(track.loved) }

            Scaffold(
                topBar = {
                    MusicorumTopBar(
                        text = track.name,
                        scrollBehavior = appBarBehavior,
                        fadeable = true
                    ) {
                        IconButton(
                            onClick = {
                                analytics.logEvent("topbar_like_pressed", null)
                                val currentlyLoved = loved.value
                                trackViewModel.updateFavoritePreference(track, currentlyLoved)
                                loved.value = !currentlyLoved
                            }) {
                            if (loved.value) {
                                Icon(Icons.Rounded.Favorite, null)
                            } else {
                                Icon(Icons.Rounded.FavoriteBorder, null)
                            }
                        }
                    }
                }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .background(KindaBlack)
                        .fillMaxWidth()
                        .nestedScroll(appBarBehavior.nestedScrollConnection)
                        .verticalScroll(screenScrollState),
                    verticalArrangement = Arrangement.Center
                ) {
                    GradientHeader(
                        track.artist.bestImageUrl,
                        track.album?.bestImageUrl,
                        RoundedCornerShape(6.dp),
                        PlaceholderType.TRACK
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = track.name,
                        softWrap = true,
                        style = Typography.displaySmall,
                        modifier = Modifier.padding(horizontal = 55.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = track.artist.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = Typography.bodyLarge,
                        color = ContentSecondary
                    )
                    HorizontalDivider(Modifier.padding(vertical = 18.dp))
                    StatisticRow(
                        short = true,
                        stringResource(R.string.listeners) to track.listeners,
                        stringResource(R.string.scrobbles) to track.playCount,
                        stringResource(R.string.your_scrobbles) to track.userPlayCount
                    )

                    track.topTags?.let {
                        Spacer(modifier = Modifier.height(20.dp))
                        TagList(tags = it.tags, coverPalette, !paletteReady)
                    }
                    track.wiki?.let {
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(modifier = Modifier.padding(horizontal = 20.dp)) {
                            ItemInformation(palette = coverPalette, info = it.summary)
                        }
                    }
                    HorizontalDivider(Modifier.padding(vertical = 20.dp))
                    ContextRow(
                        appearsOn = track.album?.name to track.album?.bestImageUrl,
                        from = track.artist.name to track.artist.bestImageUrl
                    )
                    similarTracks?.let {
                        HorizontalDivider(Modifier.padding(vertical = 20.dp))
                        Column(
                            Modifier
                                .padding(horizontal = 5.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.Start,
                        ) {
                            Section(title = stringResource(R.string.similar_tracks))
                            it.similarTracks.tracks.forEach {
                                TrackListItem(track = it, false)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun TrackLoadError(onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        modifier = Modifier
            .fillMaxSize()
            .background(KindaBlack)
    ) {
        Text(stringResource(R.string.something_went_wrong))
        OutlinedButton(onClick = onRetry) {
            Text(stringResource(R.string.retry))
        }
    }
}
