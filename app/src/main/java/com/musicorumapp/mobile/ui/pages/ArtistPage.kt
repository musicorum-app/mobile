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
import com.musicorumapp.mobile.states.LocalSnackbarContext
import com.musicorumapp.mobile.states.models.ArtistPageViewModel
import com.musicorumapp.mobile.ui.components.*
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
    val artist by viewModel.artist.observeAsState(_artist)
    val topTracks by viewModel.topTracks.observeAsState()

    val snackbarContext = LocalSnackbarContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(_artist.name) {
        viewModel.start(snackbarContext.snackbarHostState, _artist)
    }

    Column(
        modifier = Modifier.verticalScroll(scrollState)
    ) {
        ArtistContentInside(
            artist = artist,
            topTracks = topTracks,
            mainImagePainter = painterResource(LastfmEntity.ARTIST.asDrawableSource())
        )
    }

    val navContext = LocalNavigationContext.current

    FadeableAppBar(
//        alpha = Utils.interpolateValues(scrollState.value.toFloat(), 1100f, 1300f, 0f, 1f)
//            .coerceIn(0f, 1f),
        alpha = 0f,
        navigationIcon = {
            IconButton(onClick = {
                navContext.navigationController?.popBackStack()
            }) {
                Icon(imageVector = AppMaterialIcons.ArrowBack, contentDescription = "Back")
            }
        }
    ) {
        Text(artist?.name.orEmpty(), overflow = TextOverflow.Ellipsis, maxLines = 1)
    }
}

@Composable
fun ArtistContentInside(
    artist: Artist?,
    topTracks: PagingController<Track>?,
    mainImagePainter: Painter
) {
    Log.i(Constants.LOG_TAG, "----- ARTIST PAGE RECOMPOSE")
    Box {
        GradientContentHeader(artist?.name.orEmpty(), mainImagePainter = mainImagePainter)
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
            onClick = {},
            modifier = Modifier.padding(horizontal = PaddingSpacing.HorizontalMainPadding)
        ) {
            Column {
                topTracks?.getAllItems()?.take(5)?.forEach {
                    TrackListItem(track = it, modifier = Modifier.clickable { })
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Divider()
        Spacer(modifier = Modifier.height(30.dp))
        Section(
            title = stringResource(R.string.top_artists),
            onClick = {},
            headerModifier = Modifier.padding(horizontal = PaddingSpacing.HorizontalMainPadding)
        ) {
            SimilarArtists(artist?.similar)
        }
    }
}

@Composable
fun SimilarArtists(
    artists: List<Artist>? = null
) {
    LaunchedEffect(artists) {
        if (artists != null) {
            MusicorumResource.fetchArtistsResources(artists)
        }
    }

    val size = 140.dp
    val artistPlaceholderPainter = painterResource(id = LastfmEntity.ARTIST.asDrawableSource())

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.width(PaddingSpacing.HorizontalMainPadding))

            if (artists != null) {
                artists.forEach {
                        val padding = if (artists.last() === it) 0 else 12
                    Image(
                        painter = if (it.imageURL != null) rememberImagePainter(it.imageURL)
                        else artistPlaceholderPainter,
                        contentDescription = it.name,
                        modifier = Modifier
                            .padding(end = padding.dp)
                            .clip(RoundedCornerShape(size))
                            .size(size)
                    )
                }
            } else {
                for (x in 1..4) {
                    val padding = if (x == 4) 0 else 12
                    Image(
                        painter = artistPlaceholderPainter,
                        contentDescription = stringResource(R.string.artist),
                        modifier = Modifier
                            .padding(end = padding.dp)
                            .clip(RoundedCornerShape(size))
                            .size(size)
                        )
                }
            }

            Spacer(modifier = Modifier.width(PaddingSpacing.HorizontalMainPadding))
        }
    }
}

@Composable
fun SimilarArtistItem() {

}

@Preview(showBackground = true, widthDp = 400, heightDp = 400)
@Composable
fun SimilarArtistsPreview() {
    MusicorumTheme {
        Scaffold {
            Box(
                modifier = Modifier.padding(5.dp)
            ) {
                SimilarArtists()
                Spacer(modifier = Modifier.height(6.dp))
                SimilarArtists(
                    listOf(
                        Artist.fromSample(),
                        Artist.fromSample(),
                        Artist.fromSample()
                    )
                )
            }
        }
    }
}
