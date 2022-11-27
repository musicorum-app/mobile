package io.musicorum.mobile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import io.musicorum.mobile.ui.theme.LighterGray
import io.musicorum.mobile.ui.theme.MostlyRed
import java.util.*

@Composable
fun BottomNavBar(nav: NavHostController) {
    val items = listOf("Home", "Scrobbling", "Profile")
    val icons = listOf(
        Icons.Rounded.Home,
        Icons.Rounded.QueueMusic,
        Icons.Rounded.Person
    )
    val navItemColors = NavigationBarItemDefaults.colors(
        indicatorColor = MostlyRed,
        selectedIconColor = Color.White,
        selectedTextColor = Color.White
    )

    val navBackStackEntry by nav.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Box(modifier = Modifier
        .background(LighterGray)
        ) {
        NavigationBar(
            containerColor = Color.Transparent
        ) {
            items.forEachIndexed { index, s ->
                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any { it.route?.lowercase() == s.lowercase() } == true,
                    label = { Text(text = s, modifier = Modifier.padding(top = 60.dp)) },
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
}
