package com.musicorumapp.mobile.ui.pages

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.musicorumapp.mobile.R
import com.musicorumapp.mobile.api.models.Artist
import com.musicorumapp.mobile.api.models.LastfmEntity
import com.musicorumapp.mobile.api.models.MusicorumResource
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

    val painter = rememberCoilPainter(
        request = artist?.imageURL.orEmpty(),
        fadeIn = true
    )

    val snackbarContext = LocalSnackbarContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(_artist.name) {
        viewModel.start(snackbarContext.snackbarHostState, _artist)
        artist?.onResourcesChange { painter.request = it.imageURL }
    }

    Column(
        modifier = Modifier.verticalScroll(scrollState)
    ) {
        Box {
            GradientContentHeader(painter = painter, title = artist?.name.orEmpty())
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
            ) {
                SimilarArtists(artist?.similar)
            }
        }
    }

    val navContext = LocalNavigationContext.current

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
        Text(artist?.name.orEmpty(), overflow = TextOverflow.Ellipsis, maxLines = 1)
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

    val size = 80.dp
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
                    Image(
                        painter = if (it.imageURL != null) rememberImagePainter(it.imageURL)
                        else artistPlaceholderPainter,
                        contentDescription = it.name,
                        modifier = Modifier
                            .clip(RoundedCornerShape(size))
                            .size(size)
                    )
                }
            } else {
                for (x in 1..4) {
                    Image(
                        painter = artistPlaceholderPainter,
                        contentDescription = stringResource(R.string.artist),
                        modifier = Modifier
                            .clip(RoundedCornerShape(size))
                            .size(size),

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