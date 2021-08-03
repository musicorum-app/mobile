package com.musicorumapp.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.transition.Fade
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.insets.statusBarsPadding

@Composable
fun FadeableAppBar(
    alpha: Float = 1f,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable () -> Unit
) {
    FadeableAppBarContent(
        alpha = alpha,
        navigationIcon = navigationIcon,
        actions = actions
    ) {
        Box(modifier = Modifier.alpha(alpha)) {
            content()
        }
    }
}

@Composable
fun FadeableAppBarContent(
    alpha: Float,
    navigationIcon: @Composable (() -> Unit)?,
    actions: @Composable RowScope.() -> Unit,
    content: @Composable () -> Unit,
) {
    val color = MaterialTheme.colors.primarySurface.copy(alpha = alpha)
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsHeight()
                .background(color)
        )
        TopAppBar(
            title = content,
            navigationIcon = navigationIcon,
            actions = actions,
            backgroundColor = color,
            elevation = 0.dp
        )
    }
}