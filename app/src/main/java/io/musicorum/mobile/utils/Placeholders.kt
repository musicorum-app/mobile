package io.musicorum.mobile.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import io.musicorum.mobile.R

enum class Placeholders {
    TRACK;

    @Composable
    fun asPainter(): Painter {
        when (this) {
            TRACK -> return painterResource(R.drawable.track_placeholder)
        }
    }
}