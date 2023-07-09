package io.musicorum.mobile.views.login

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink

fun NavGraphBuilder.loginGraph(navController: NavController) {
    val callbackDeeplink = navDeepLink {
        uriPattern = "musicorum://auth-callback?token={token}"
    }

    navigation(startDestination = "auth", route = "login") {
        composable("auth", deepLinks = listOf(callbackDeeplink)) {
            Login(nav = navController, it.arguments?.getString("token"))
        }
        composable(
            "user_confirmation/{session_key}",
            arguments = listOf(navArgument("session_key") { type = NavType.StringType })
        ) {
            UserConfirmation(nav = navController, it.arguments?.getString("session_key")!!)
        }

        composable("deviceScrobble") { Scrobble() }
    }
}