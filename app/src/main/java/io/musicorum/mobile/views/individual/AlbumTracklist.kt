package io.musicorum.mobile.views.individual

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import io.musicorum.mobile.components.AlbumTrack
import io.musicorum.mobile.components.CenteredLoadingSpinner
import io.musicorum.mobile.components.MusicorumTopBar
import io.musicorum.mobile.viewmodels.AlbumViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumTracklist(partialAlbum: PartialAlbum, model: AlbumViewModel = viewModel()) {
    val album = model.album.observeAsState().value?.album

    LaunchedEffect(Unit) {
        model.getAlbum(partialAlbum.name, partialAlbum.artist)
    }

    if (album == null) {
        CenteredLoadingSpinner()
    } else {
        Scaffold(topBar = {
            MusicorumTopBar(
                text = "Album Tracks",
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                fadeable = false
            ) {}
        }) {
            LazyColumn(modifier = Modifier.padding(it)) {
                album.tracks?.let { trackList ->
                    itemsIndexed(trackList) { i, track ->
                        AlbumTrack(position = i + 1, name = track.name)
                    }
                }
            }
        }
    }
}