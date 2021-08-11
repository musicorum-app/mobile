package com.musicorumapp.mobile.ui.pages

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.musicorumapp.mobile.Constants
import com.musicorumapp.mobile.R
import com.musicorumapp.mobile.api.models.*
import com.musicorumapp.mobile.states.LocalAuth
import com.musicorumapp.mobile.states.LocalNavigationContext
import com.musicorumapp.mobile.states.LocalNavigationContextContent
import com.musicorumapp.mobile.states.LocalSnackbarContext
import com.musicorumapp.mobile.states.models.ArtistPageViewModel
import com.musicorumapp.mobile.ui.components.*
import com.musicorumapp.mobile.ui.pages.artist.SimilarArtists
import com.musicorumapp.mobile.ui.theme.AppMaterialIcons
import com.musicorumapp.mobile.ui.theme.MusicorumTheme
import com.musicorumapp.mobile.ui.theme.PaddingSpacing
import com.musicorumapp.mobile.utils.Utils

@Composable
fun ArtistPage(
    artist: Artist?
) {
    if (artist == null) {
        ArtistNotFound()
    } else {
        ArtistContent(
            artist,
            viewModel = hiltViewModel()
        )
    }
}

@Composable
fun ArtistNotFound() {
    Text("not found x.x")
}

@Composable
fun ArtistContent(
    _artist: Artist,
    viewModel: ArtistPageViewModel
) {
    val fetched by viewModel.fetched.observeAsState(false)

    val artist by viewModel.artist.observeAsState(_artist)
    val topTracks by viewModel.topTracks.observeAsState()
    val topAlbums by viewModel.topAlbums.observeAsState()

    val snackbarContext = LocalSnackbarContext.current
    val scrollState = rememberScrollState()

    val painter = rememberImagePainter(
        artist?.imageURL,
        builder = {
            crossfade(true)
            placeholder(LastfmEntity.ARTIST.asDrawableSource())
        }
    )

    val backgroundPainter = rememberImagePainter(
        topAlbums?.getPageContent(1)?.first()?.imageURL,
        builder = {
            crossfade(true)
        }
    )

    LaunchedEffect(fetched) {
        println("FETCHED VALUE --- $fetched")
        if (!fetched) viewModel.start(snackbarContext.snackbarHostState, _artist)
    }
    val navContext = LocalNavigationContext.current

    println("---------------------- ARTIST IMAGE ${artist?.imageURL}")

    ArtistContentInside(
        artist = artist,
        topTracks = topTracks,
        scrollState = scrollState,
        mainImagePainter = painter,
        backgroundPainter = backgroundPainter,
    )
    ArtistAppBar(scrollState = scrollState, navContext = navContext, name = artist?.name)
}

@Composable
fun ArtistContentInside(
    artist: Artist?,
    topTracks: PagingController<Track>?,
    scrollState: ScrollState,
    mainImagePainter: Painter,
    backgroundPainter: Painter
) {

    val top5tracks = topTracks?.getAllItems()?.take(5)

    Column(
        modifier = Modifier.verticalScroll(scrollState)
    ) {
        Box {
            GradientContentHeader(artist?.name.orEmpty(),
                mainImagePainter = mainImagePainter,
                backgroundPainter = backgroundPainter
            )
        }
        Column {
            Stats(
                stats = mapOf(
                    stringResource(R.string.listeners) to artist?.listeners,
                    stringResource(R.string.scrobbles) to artist?.playCount,
                    stringResource(R.string.your_scrobbles) to artist?.userPlayCount,
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            Tags(artist?.tags)
            Spacer(modifier = Modifier.height(25.dp))
            Divider()
            Spacer(modifier = Modifier.height(25.dp))
            Section(
                title = stringResource(R.string.top_tracks),
                modifier = Modifier.padding(horizontal = PaddingSpacing.HorizontalMainPadding)
            ) {
                Column {
                    if (top5tracks != null) {
                        top5tracks.forEach {
                            TrackListItem(track = it, modifier = Modifier.clickable { })
                        }
                    } else {
                        for (x in 1..5) {
                            TrackListItem(null)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Divider()
            Spacer(modifier = Modifier.height(30.dp))
            Section(
                title = stringResource(R.string.similar_artists),
                headerModifier = Modifier.padding(horizontal = PaddingSpacing.HorizontalMainPadding)
            ) {
                SimilarArtists(artist?.similar)
            }
        }
    }
}

@Composable
fun ArtistAppBar(
    scrollState: ScrollState,
    navContext: LocalNavigationContextContent,
    name: String?
) {
    FadeableAppBar(
        alpha = Utils.interpolateValues(scrollState.value.toFloat(), 1100f, 1300f, 0f, 1f)
            .coerceIn(0f, 1f),
        navigationIcon = {
            IconButton(onClick = {
                navContext.navigationController?.popBackStack()
            }) {
                Icon(imageVector = AppMaterialIcons.ArrowBack, contentDescription = "Back")
            }
        }
    ) {
        Text(name.orEmpty(), overflow = TextOverflow.Ellipsis, maxLines = 1)
    }
}
