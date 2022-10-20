package io.musicorum.mobile.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import io.musicorum.mobile.ui.theme.DarkGray

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MusicorumTopBar(text: String, scrollBehavior: TopAppBarScrollBehavior) {
    val nav = rememberAnimatedNavController()
    val color = TopAppBarDefaults.mediumTopAppBarColors(
        containerColor = DarkGray
    )

    TopAppBar(
        title = { Text(text = text) },
        navigationIcon = {
            IconButton(onClick = { nav.popBackStack() }) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "")
            }
        },
        scrollBehavior = scrollBehavior,
        colors = color,
    )
}