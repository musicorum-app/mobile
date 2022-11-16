package io.musicorum.mobile.utils

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import io.musicorum.mobile.R

@Composable
fun NowPlaying(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            View.inflate(ctx, R.layout.nowplaying_view, null)
        })
}
