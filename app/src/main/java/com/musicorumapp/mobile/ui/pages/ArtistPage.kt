package com.musicorumapp.mobile.ui.pages

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.coil.rememberCoilPainter
import com.musicorumapp.mobile.api.models.Artist
import com.musicorumapp.mobile.api.models.MusicorumResource
import com.musicorumapp.mobile.states.LocalAuth
import com.musicorumapp.mobile.states.LocalNavigationContext
import com.musicorumapp.mobile.states.models.ArtistPageViewModel
import com.musicorumapp.mobile.states.models.DiscoverPageViewModel
import com.musicorumapp.mobile.ui.components.FadeableAppBar
import com.musicorumapp.mobile.ui.components.GradientContentHeader
import com.musicorumapp.mobile.ui.theme.AppMaterialIcons

@Composable
fun ArtistPage(
    artist: Artist?
) {
    if (artist == null) {
        ArtistNotFound()
    } else {
        ArtistContent(artist)
    }
}


@Composable
fun ArtistContent(artist: Artist) {
    val painter = rememberCoilPainter(
        request = artist.imageURL.orEmpty(),
        fadeIn = true
    )

    val user = LocalAuth.current.user
    val artistPageViewModel: ArtistPageViewModel =
        viewModel(factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return ArtistPageViewModel(artist, user!!) as T
            }
        })

    LaunchedEffect("asd") {
        MusicorumResource.fetchArtistsResources(listOf(artist))

    }

    artist.onResourcesChange { painter.request = it.imageURL }

    GradientContentHeader(painter = painter, title = artist.name)

    val navContext = LocalNavigationContext.current

    FadeableAppBar(
        alpha = 0.3f,
        navigationIcon = {
            IconButton(onClick = {
                navContext.navigationController?.popBackStack()
            }) {
                Icon(imageVector = AppMaterialIcons.ArrowBack, contentDescription = "Back")
            }
        }
    ) {
        Text(artist.name, overflow = TextOverflow.Ellipsis, maxLines = 1)
    }
}


@Composable
fun ArtistNotFound() {
    Text("not found x.x")
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ArtistContentPreview() {
    ArtistContent(artist = Artist.fromSample())
}