package com.musicorumapp.mobile.ui.pages.artist

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.musicorumapp.mobile.R
import com.musicorumapp.mobile.api.models.*
import com.musicorumapp.mobile.states.LocalNavigationContext
import com.musicorumapp.mobile.states.LocalNavigationContextContent
import com.musicorumapp.mobile.states.LocalSnackbarContext
import com.musicorumapp.mobile.states.models.ArtistPageViewModel
import com.musicorumapp.mobile.ui.components.*
import com.musicorumapp.mobile.ui.theme.AppMaterialIcons
import com.musicorumapp.mobile.ui.theme.KindaBlack
import com.musicorumapp.mobile.ui.theme.MusicorumTheme
import com.musicorumapp.mobile.ui.theme.PaddingSpacing
import com.musicorumapp.mobile.utils.Utils
import com.musicorumapp.mobile.utils.calculateColorContrast
import com.musicorumapp.mobile.utils.rememberPredominantColor

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
    val predominantColor by viewModel.predominantColor.observeAsState()
    val imageBitmap by viewModel.imageBitmap.observeAsState()

    val snackbarContext = LocalSnackbarContext.current
    val scrollState = rememberScrollState()

    val predominantColorState = rememberPredominantColor(
        colorFinder = {
            it.lightVibrantSwatch ?: it.vibrantSwatch ?: it.swatches.maxByOrNull { s ->
                calculateColorContrast(
                    Color(s.rgb), KindaBlack
                )
            }
        }
    )

    val painter = rememberImagePainter(
        imageBitmap,
        builder = {
            crossfade(true)
            placeholder(LastfmEntity.ARTIST.asDrawableSource())
        }
    )

    val backgroundPainter = rememberImagePainter(
        data = topAlbums?.getPageContent(1)?.first()?.images?.getCustomSizeImage(600),
        builder = {
            crossfade(true)
        },
    )

    val context = LocalContext.current

    LaunchedEffect(fetched) {
        if (!fetched) viewModel.start(
            context,
            predominantColorState,
            snackbarContext.snackbarHostState,
            _artist
        )
    }
    val navContext = LocalNavigationContext.current

    println("---------------------- ARTIST IMAGE ${artist?.imageURL}")

    ArtistContentInside(
        artist = artist,
        topTracks = topTracks,
        scrollState = scrollState,
        mainImagePainter = painter,
        backgroundPainter = backgroundPainter,
        predominantColor = predominantColor
    )
    ArtistAppBar(
        scrollState = scrollState,
        navContext = navContext,
        name = artist?.name,
        mainImagePainter = painter
    )
}

@Composable
fun ArtistContentInside(
    artist: Artist?,
    topTracks: PagingController<Track>?,
    scrollState: ScrollState,
    mainImagePainter: Painter,
    backgroundPainter: Painter,
    predominantColor: Color?
) {

    val top5tracks = topTracks?.getAllItems()?.take(5)

    Column(
        modifier = Modifier.verticalScroll(scrollState)
    ) {
        Box {
            GradientContentHeader(
                artist?.name.orEmpty(),
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
            Tags(artist?.tags, color = predominantColor)
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
    mainImagePainter: Painter,
    name: String?
) {
    FadeableAppBar(
        alpha = Utils.interpolateValues(scrollState.value.toFloat(), 1100f, 1500f, 0f, 1f)
            .coerceIn(0f, 1f),
        navigationIcon = {
            IconButton(onClick = {
                navContext.navigationController?.popBackStack()
            }) {
                Icon(imageVector = AppMaterialIcons.ArrowBack, contentDescription = "Back")
            }
        }
    ) {
        val size = 32.dp

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {


            Box(
                modifier = Modifier
                    .padding(end = 6.dp)
                    .clip(RoundedCornerShape(size))
                    .size(size)
            ) {
                Image(
                    painter = painterResource(id = LastfmEntity.ARTIST.asDrawableSource()),
                    contentDescription = null,
                    modifier = Modifier.size(size)
                )
                Image(
                    painter = mainImagePainter,
                    contentDescription = null,
                    modifier = Modifier.size(size)
                )
            }

            Text(
                name.orEmpty(),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.offset(y = 2.dp)
            )
        }
    }
}
