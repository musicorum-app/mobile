package io.musicorum.mobile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import io.musicorum.mobile.ui.theme.KindaBlack
import java.util.*

@Composable
fun BottomNavBar(current: String, nav: NavHostController) {
    val items = listOf("Home", "Discover", "Scrobbling", "Charts", "Account")
    val icons = listOf(
        Icons.Rounded.Home,
        Icons.Rounded.Search,
        Icons.Rounded.QueueMusic,
        Icons.Rounded.BarChart,
        Icons.Rounded.Person
    )

    Box(modifier = Modifier.background(KindaBlack)) {
        NavigationBar(
            containerColor = Color.Transparent
        ) {
            items.forEachIndexed { index, s ->
                NavigationBarItem(
                    selected = s == current,
                    label = { Text(text = s) },
                    onClick = { nav.navigate(s.lowercase(Locale.ROOT)) },
                    icon = { Icon(icons[index], contentDescription = "nav icon") },
                    alwaysShowLabel = false,
                )

            }
        }
    }
}