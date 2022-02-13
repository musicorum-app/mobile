package com.musicorumapp.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.google.accompanist.insets.statusBarsHeight

@Composable
fun FadeableAppBar(
    alpha: Float = 1f,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable () -> Unit
) {
    FadeableAppBarContent(
        alpha = alpha,
        navigationIcon = navigationIcon,
        actions = actions
    ) {
        Box(modifier = Modifier.graphicsLayer(
            alpha = alpha
        )) {
            content()
        }
    }
}

@Composable
fun FadeableAppBarContent(
    alpha: Float,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit,
    content: @Composable () -> Unit,
) {
    val color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = alpha)
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsHeight()
                .background(color)
        )
        SmallTopAppBar(
            title = content,
            navigationIcon = navigationIcon,
            actions = actions
        )
    }
}