package io.musicorum.mobile.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import io.musicorum.mobile.R

enum class Placeholders {
    TRACK, USER, ARTIST, ALBUM;

    @Composable
    fun asPainter(): Painter {
        return when (this) {
            TRACK -> painterResource(R.drawable.track_placeholder)
            USER -> painterResource(R.drawable.user)
            ARTIST -> painterResource(R.drawable.artist_placeholder)
            ALBUM -> painterResource(R.drawable.album_placeholder)
        }
    }

    fun asDrawable(): Int {
        return when (this) {
            TRACK -> R.drawable.track_placeholder
            USER -> R.drawable.user
            ARTIST -> R.drawable.artist_placeholder
            ALBUM -> R.drawable.album_placeholder
        }
    }
}