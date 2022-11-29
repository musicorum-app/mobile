package io.musicorum.mobile.router

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import io.musicorum.mobile.screens.*
import io.musicorum.mobile.screens.individual.Album
import io.musicorum.mobile.screens.individual.Track
import io.musicorum.mobile.screens.individual.User
import io.musicorum.mobile.screens.login.loginGraph

private val startTransition = slideInHorizontally(tween(800)) { fullWidth -> fullWidth }
private val exitTransition = slideOutHorizontally(tween(800)) { fullWidth -> -fullWidth / 2 }
private val popExitTransition = slideOutHorizontally(tween(800)) { fullWidth -> fullWidth / 2 }
private val popEnterTransition = slideInHorizontally(tween(800)) { fullWidth -> -fullWidth }

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavigationRouter(controller: NavHostController) {
    AnimatedNavHost(
        navController = controller,
        startDestination = "home",
        enterTransition = { startTransition },
        exitTransition = { exitTransition },
        popExitTransition = { popExitTransition },
        popEnterTransition = { popEnterTransition }
    ) {
        loginGraph(navController = controller)
        composable("home") {
            Home(nav = controller)
        }

        composable("recentScrobbles") {
            RecentScrobbles()
        }

/*        composable("mostListened") {
            MostListened(
                mostListenedViewModel = mostListenedViewModel,
                nav = controller
            )
        }*/

        composable(
            "user/{username}",
            arguments = listOf(navArgument("username") {
                type = NavType.StringType
            })
        ) {
            User(
                username = it.arguments?.getString("username")!!
            )
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
            Track(
                it.arguments?.getString("trackData"),
                nav = controller
            )
        }
    }
}
