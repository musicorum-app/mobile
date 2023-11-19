package io.musicorum.mobile.views.individual

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.palette.graphics.Palette
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import io.musicorum.mobile.LocalAnalytics
import io.musicorum.mobile.R
import io.musicorum.mobile.coil.PlaceholderType
import io.musicorum.mobile.components.*
import io.musicorum.mobile.ui.theme.KindaBlack
import io.musicorum.mobile.ui.theme.Typography
import io.musicorum.mobile.utils.createPalette
import io.musicorum.mobile.utils.getBitmap
import io.musicorum.mobile.viewmodels.ArtistViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Artist(artistName: String, artistViewModel: ArtistViewModel = viewModel()) {
    val analytics = LocalAnalytics.current
    LaunchedEffect(Unit) {
        analytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, "artist")
        }
    }
    val artist = artistViewModel.artist.observeAsState().value
    val topAlbums by artistViewModel.topAlbums.observeAsState()
    val topTracks = artistViewModel.topTracks.observeAsState().value
    val palette = remember { mutableStateOf<Palette?>(null) }
    val paletteReady = remember { mutableStateOf(false) }
    val ctx = LocalContext.current

    LaunchedEffect(artist) {
        if (artist == null) {
            artistViewModel.fetchArtist(artistName)
            artistViewModel.fetchTopAlbums(artistName)
            artistViewModel.fetchTopTracks(artistName)
        } else {
            val bmp = getBitmap(artist.bestImageUrl, ctx)
            val p = createPalette(bmp)
            palette.value = p
            paletteReady.value = true
        }
    }

    if (artist == null) {
        CenteredLoadingSpinner()
    } else {
        val appBarState = rememberTopAppBarState(initialContentOffset = 700f)
        val appBarBehavior =
            TopAppBarDefaults.pinnedScrollBehavior(state = appBarState)
        Scaffold(
            topBar = {
                MusicorumTopBar(
                    text = artist.name,
                    scrollBehavior = appBarBehavior,
                    fadeable = true
                ) {}
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(KindaBlack)
                    .nestedScroll(appBarBehavior.nestedScrollConnection)
                    .verticalScroll(rememberScrollState())
            ) {
                GradientHeader(
                    backgroundUrl = topAlbums?.getOrNull(0)?.bestImageUrl,
                    coverUrl = artist.bestImageUrl,
                    shape = CircleShape,
                    placeholderType = PlaceholderType.ARTIST
                )
                Text(
                    text = artist.name,
                    style = Typography.displaySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 18.dp))
                StatisticRow(
                    short = true,
                    stringResource(id = R.string.listeners) to artist.stats?.listeners,
                    stringResource(id = R.string.scrobbles) to artist.stats?.playCount,
                    stringResource(id = R.string.your_scrobbles) to artist.stats?.userPlayCount
                )
                Spacer(modifier = Modifier.height(20.dp))

                artist.tags?.let {
                    TagList(
                        tags = it.tags,
                        referencePalette = palette.value,
                        visible = !paletteReady.value
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))

                artist.bio?.let {
                    Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                        ItemInformation(palette = palette.value, info = it.summary)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 18.dp))
                Section(title = "Top Tracks")
                topTracks?.let {
                    TrackListItem(track = it.getOrNull(0), favoriteIcon = false)
                    TrackListItem(track = it.getOrNull(1), favoriteIcon = false)
                    TrackListItem(track = it.getOrNull(2), favoriteIcon = false)
                    TrackListItem(track = it.getOrNull(3), favoriteIcon = false)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Section(title = "Top Albums")
                topAlbums?.let {
                    TopAlbumsRow(albums = it)
                }
                Spacer(modifier = Modifier.height(20.dp))
                Section(title = "Similar to $artistName")

                artist.similar?.let {
                    ArtistRow(artists = it.artist)
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}