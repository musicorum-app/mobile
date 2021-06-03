package com.musicorumapp.mobile.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.IntSize

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PageAnimation(content: @Composable () -> Unit) {
    println("a TRANSITION")
    val visible = remember { mutableStateOf(false) }

    DisposableEffect("2") {
        visible.value = true
        onDispose {
            visible.value = false
        }
    }

    println(visible.value)

    AnimatedVisibility(
        visible = visible.value,
        enter = fadeIn() + expandIn(
            expandFrom = Alignment.TopStart,
            initialSize = {
                IntSize(
                    width = (it.width * 0.8).toInt(),
                    height = (it.height * 0.8).toInt()
                )
            },

        ),
        exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.Center)
    ) {
        content()
    }
}