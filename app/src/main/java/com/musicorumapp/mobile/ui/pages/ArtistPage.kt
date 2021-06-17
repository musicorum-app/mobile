package com.musicorumapp.mobile.ui.pages

import androidx.compose.material.Text
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.coil.rememberCoilPainter
import com.musicorumapp.mobile.api.models.Artist
import com.musicorumapp.mobile.api.models.MusicorumResource
import com.musicorumapp.mobile.ui.components.FadeableAppBar
import com.musicorumapp.mobile.ui.components.GradientContentHeader

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
        request = artist.getImageURL().orEmpty(),
        fadeIn = true
    )

    LaunchedEffect("asd") {
        MusicorumResource.fetchArtistsResources(listOf(artist))
    }

    artist.onResourcesChange { painter.request = it.getImageURL() }

    GradientContentHeader(painter = painter)

    FadeableAppBar {
        
    }
}


@Composable
fun ArtistNotFound() {
    Text("not found x.x")
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ArtistContentPreview () {
    ArtistContent(artist = Artist.fromSample())
}