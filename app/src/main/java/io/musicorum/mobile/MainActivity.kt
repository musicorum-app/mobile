package io.musicorum.mobile

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import io.musicorum.mobile.components.BottomNavBar
import io.musicorum.mobile.screens.*
import io.musicorum.mobile.screens.individual.Track
import io.musicorum.mobile.screens.individual.User
import io.musicorum.mobile.ui.theme.MusicorumMobileTheme
import io.musicorum.mobile.viewmodels.HomeViewModel
import io.musicorum.mobile.viewmodels.MostListenedViewModel

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "userdata")

class MainActivity : ComponentActivity() {
    @OptIn(
        ExperimentalAnimationApi::class,
        ExperimentalMaterial3Api::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberAnimatedNavController()
            val authedUser: HomeViewModel = viewModel()
            val mostListenedViewModel: MostListenedViewModel = viewModel()

            MusicorumMobileTheme {
                Scaffold(
                    bottomBar = { BottomNavBar(nav = navController) }
                ) { paddingValues ->
                    Surface(
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        AnimatedNavHost(
                            navController = navController,
                            startDestination = "home",
                            enterTransition = { slideInHorizontally(tween(800)) { fullWidth -> fullWidth } },
                            exitTransition = { slideOutHorizontally(tween(800)) { fullWidth -> -fullWidth / 2 } },
                            popExitTransition = { slideOutHorizontally(tween(800)) { fullWidth -> fullWidth / 2 } },
                            popEnterTransition = { slideInHorizontally(tween(800)) { fullWidth -> -fullWidth } }
                        ) {
                            composable("login", deepLinks = listOf(navDeepLink {
                                uriPattern = "musicorum://auth-callback?token={token}"
                            })) {
                                Login(nav = navController, it.arguments?.getString("token"))
                            }

                            composable("home") {
                                Home(
                                    homeViewModel = authedUser,
                                    nav = navController
                                )
                            }
                            composable("recentScrobbles") {
                                RecentScrobbles(homeViewModel = authedUser, nav = navController)
                            }
                            composable("mostListened") {
                                MostListened(
                                    homeViewModel = authedUser,
                                    mostListenedViewModel = mostListenedViewModel,
                                    nav = navController
                                )
                            }

                            composable(
                                "user/{username}",
                                arguments = listOf(navArgument("username") {
                                    type = NavType.StringType
                                })
                            ) {
                                User(
                                    username = it.arguments?.getString("username")!!,
                                    nav = navController
                                )
                            }

                            composable("discover") { Discover() }
                            composable("scrobbling") { Scrobbling() }
                            composable("charts") { Charts() }
                            composable("account") { Account() }

                            composable(
                                "track/{trackData}",
                                arguments = listOf(navArgument("trackData") {
                                    type = NavType.StringType
                                })
                            ) {
                                Track(
                                    it.arguments?.getString("trackData"),
                                    homeViewModel = authedUser,
                                    nav = navController
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}