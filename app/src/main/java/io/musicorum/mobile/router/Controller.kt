package io.musicorum.mobile.router

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import io.musicorum.mobile.views.Account
import io.musicorum.mobile.views.Discover
import io.musicorum.mobile.views.Home
import io.musicorum.mobile.views.RecentScrobbles
import io.musicorum.mobile.views.Scrobbling
import io.musicorum.mobile.views.charts.Charts
import io.musicorum.mobile.views.individual.Album
import io.musicorum.mobile.views.individual.Track
import io.musicorum.mobile.views.individual.User
import io.musicorum.mobile.views.login.loginGraph

private val startTransition = slideInHorizontally(tween(800)) { fullWidth -> fullWidth }
private val exitTransition = slideOutHorizontally(tween(800)) { fullWidth -> -fullWidth / 2 }
private val popExitTransition = slideOutHorizontally(tween(800)) { fullWidth -> fullWidth / 2 }
private val popEnterTransition = slideInHorizontally(tween(800)) { fullWidth -> -fullWidth }

@Composable
fun NavigationRouter(controller: NavHostController) {
    NavHost(
        navController = controller,
        startDestination = "home",
        enterTransition = { startTransition },
        exitTransition = { exitTransition },
        popExitTransition = { popExitTransition },
        popEnterTransition = { popEnterTransition }
    ) {
        loginGraph(navController = controller)
        composable("home") { Home() }

        composable("recentScrobbles") { RecentScrobbles() }

        /*        composable("mostListened") {
                    MostListened(
                        mostListenedViewModel = mostListenedViewModel,
                        nav = controller
                    )
                }*/

        composable(
            "user/{usernameArg}",
            arguments = listOf(navArgument("usernameArg") {
                type = NavType.StringType
            })
        ) {
            User()
        }

        composable(
            "album/{albumData}",
            arguments = listOf(navArgument("albumData") {
                type = NavType.StringType
            }),
        ) {
            Album(it.arguments?.getString("albumData"), nav = controller)
        }

        composable("discover") { Discover() }
        composable("scrobbling") { Scrobbling() }
        composable("charts") { Charts() }
        composable("profile") { Account() }

        composable(
            "track/{trackData}",
            arguments = listOf(navArgument("trackData") {
                type = NavType.StringType
            })
        ) {
            Track(it.arguments?.getString("trackData"))
        }
    }
}
