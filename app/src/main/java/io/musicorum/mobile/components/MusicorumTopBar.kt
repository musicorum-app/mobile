package io.musicorum.mobile.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicorumTopBar(
    text: String,
    scrollBehavior: TopAppBarScrollBehavior,
    nav: NavHostController,
    fadeable: Boolean,
    likeAction: @Composable RowScope.() -> Unit
) {
    val colors = TopAppBarDefaults.smallTopAppBarColors(
        scrolledContainerColor = Color.DarkGray,
        containerColor = Color.Transparent
    )
    val fraction = scrollBehavior.state.overlappedFraction
    val modifier = if (fadeable) {
        Modifier
            .padding(top = 45.dp)
            .alpha(fraction)
    } else {
        Modifier.padding(top = 45.dp)
    }



    TopAppBar(
        title = {
            Text(
                text = text, modifier = modifier
            )
        },
        navigationIcon = {
            IconButton(onClick = { nav.popBackStack() }, modifier = Modifier.padding(top = 37.dp)) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "")
            }
        },
        scrollBehavior = scrollBehavior,
        colors = colors,
        actions = likeAction,
        modifier = Modifier.fillMaxHeight(0.12f)
    )

}