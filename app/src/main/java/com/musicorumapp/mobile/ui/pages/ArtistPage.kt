package com.musicorumapp.mobile.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.coil.rememberCoilPainter
import com.musicorumapp.mobile.R
import com.musicorumapp.mobile.api.models.Artist
import com.musicorumapp.mobile.states.LocalAuth
import com.musicorumapp.mobile.states.LocalNavigationContext
import com.musicorumapp.mobile.states.LocalSnackbarContext
import com.musicorumapp.mobile.states.models.ArtistPageViewModel
import com.musicorumapp.mobile.ui.components.FadeableAppBar
import com.musicorumapp.mobile.ui.components.GradientContentHeader
import com.musicorumapp.mobile.ui.components.Stats
import com.musicorumapp.mobile.ui.theme.AppMaterialIcons

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
fun ArtistContent(
    _artist: Artist,
    viewModel: ArtistPageViewModel
) {
    val artist by viewModel.artist.observeAsState(_artist)
    println("COMPOSE ARTIST: $artist")
    val painter = rememberCoilPainter(
        request = artist?.imageURL.orEmpty(),
        fadeIn = true
    )

    val snackbarContext = LocalSnackbarContext.current
    LaunchedEffect(_artist.name) {
        viewModel.start(snackbarContext.snackbarHostState, _artist)
    }

    artist?.onResourcesChange { painter.request = it.imageURL }

    val scrollState = rememberScrollState()

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
        }
    }

    val navContext = LocalNavigationContext.current

    FadeableAppBar(
        alpha = (scrollState.value / 200f).coerceIn(0f, 1f),
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
fun ArtistNotFound() {
    Text("not found x.x")
}