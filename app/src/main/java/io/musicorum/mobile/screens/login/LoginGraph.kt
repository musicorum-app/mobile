package io.musicorum.mobile.screens.login

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
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
    }
}