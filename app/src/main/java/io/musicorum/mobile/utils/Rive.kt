package io.musicorum.mobile.utils

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

class Rive {
    companion object {
        @Composable
        fun AnimationFor(id: Int, modifier: Modifier) {
            return AndroidView(
                modifier = modifier,
                factory = { ctx ->
                    View.inflate(ctx, id, null)
                })
        }
    }
}