package io.musicorum.mobile.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import io.musicorum.mobile.ui.theme.DarkGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicorumTopBar(text: String, scrollBehavior: TopAppBarScrollBehavior, nav: NavHostController) {
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