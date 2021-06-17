package com.musicorumapp.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.transition.Fade
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.insets.statusBarsPadding

@Composable
fun FadeableAppBar(
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.graphicsLayer { alpha = 0.3f }) {
        FadeableAppBarContent {
            content()
        }
    }
}

@Composable
fun FadeableAppBarContent (
    content: @Composable () -> Unit
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsHeight()
                .background(MaterialTheme.colors.primarySurface)
        )
        TopAppBar(
            elevation = 0.dp,
        ) {
            content()
        }
    }
}