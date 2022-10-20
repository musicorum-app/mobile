package io.musicorum.mobile.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import io.musicorum.mobile.ui.theme.KindaBlack
import io.musicorum.mobile.ui.theme.MostlyRed
import java.util.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BottomNavBar(nav: NavHostController) {
    val items = listOf("Home", "Discover", "Scrobbling", "Charts", "Account")
    val icons = listOf(
        Icons.Rounded.Home,
        Icons.Rounded.Search,
        Icons.Rounded.QueueMusic,
        Icons.Rounded.BarChart,
        Icons.Rounded.Person
    )
    val navItemColors = NavigationBarItemDefaults.colors(
        indicatorColor = MostlyRed,
        selectedIconColor = Color.White,
        selectedTextColor = Color.White
    )

    val navBackStackEntry by nav.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Box(
        modifier = Modifier
            .background(KindaBlack)
    ) {
        NavigationBar(
            containerColor = Color.Transparent
        ) {
            items.forEachIndexed { index, s ->
                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any { it.route?.lowercase() == s.lowercase() } == true,
                    label = { Text(text = s, modifier = Modifier.padding(top = 60.dp)) },
                    onClick = { nav.navigate(s.lowercase(Locale.ROOT)) },
                    icon = { Icon(icons[index], contentDescription = "nav icon") },
                    alwaysShowLabel = false,
                    colors = navItemColors
                )

            }
        }
    }
}