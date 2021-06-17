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
    val visible = remember { mutableStateOf(false) }

    DisposableEffect("2") {
        visible.value = true
        onDispose {
            visible.value = false
        }
    }

    AnimatedVisibility(
        visible = visible.value,
        enter = fadeIn() + slideInHorizontally(
            initialOffsetX = { -60 }

        ),
        exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.CenterHorizontally)
    ) {
        content()
    }
}