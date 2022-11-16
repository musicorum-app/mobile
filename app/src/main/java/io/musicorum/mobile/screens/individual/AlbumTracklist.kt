package io.musicorum.mobile.screens.individual

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import io.musicorum.mobile.components.AlbumTrack
import io.musicorum.mobile.serialization.Track

@Composable
fun AlbumTracklist(tracks: List<Track>) {
    LazyColumn {
        itemsIndexed(tracks) { i, track ->
            AlbumTrack(position = i + 1, name = track.name)
        }
    }
}