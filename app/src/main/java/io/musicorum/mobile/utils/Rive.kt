package io.musicorum.mobile.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import app.rive.runtime.kotlin.RiveAnimationView

class Rive {
    companion object {
        @Composable
        fun AnimationFor(id: Int, modifier: Modifier = Modifier, _alpha: Float = 1f) {
            AndroidView(
                modifier = modifier,
                factory = { ctx ->
                    RiveAnimationView(ctx).apply {
                        setRiveResource(id)
                        alpha = _alpha
                    }
                })

        }
    }
}