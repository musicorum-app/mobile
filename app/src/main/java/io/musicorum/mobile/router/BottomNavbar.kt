package io.musicorum.mobile.router

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import io.musicorum.mobile.LocalNavigation
import io.musicorum.mobile.ui.theme.LighterGray
import io.musicorum.mobile.ui.theme.MostlyRed
import java.util.Locale

@Composable
fun BottomNavBar() {
    val items = listOf("Home", "Discover", "Scrobbling", "Charts", "Profile")
    val nav = LocalNavigation.current!!
    val icons = listOf(
        Icons.Rounded.Home,
        Icons.Rounded.Search,
        Icons.AutoMirrored.Rounded.QueueMusic,
        Icons.Rounded.BarChart,
        Icons.Rounded.Person
    )
    val navItemColors = NavigationBarItemDefaults.colors(
        indicatorColor = MostlyRed,
        selectedIconColor = Color.White,
        selectedTextColor = Color.White
    )

    val navBackStackEntry = nav.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination

    NavigationBar(containerColor = LighterGray) {
        items.forEachIndexed { index, s ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.route?.lowercase() == s.lowercase() } == true,
                label = { Text(text = s, maxLines = 1) },
                onClick = {
                    nav.navigate(s.lowercase(Locale.ROOT))
                    {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(nav.graph.findStartDestination().id) {
                            saveState = true
                        }
                    }
                },
                icon = { Icon(icons[index], contentDescription = "nav icon") },
                alwaysShowLabel = false,
                colors = navItemColors
            )
        }
    }
}
