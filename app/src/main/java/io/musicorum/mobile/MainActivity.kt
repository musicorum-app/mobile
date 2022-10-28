package io.musicorum.mobile

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import io.musicorum.mobile.ktor.endpoints.AuthEndpoint
import io.musicorum.mobile.screens.*
import io.musicorum.mobile.screens.individual.Track
import io.musicorum.mobile.ui.theme.MusicorumMobileTheme
import io.musicorum.mobile.viewmodels.HomeViewModel
import io.musicorum.mobile.viewmodels.MostListenedViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "userdata")

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class, DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deepLinkData: Uri? = intent.data
        val sharedPref = this.getPreferences(MODE_PRIVATE)
        val dataStore = this.applicationContext.dataStore


        setContent {
            val navController = rememberAnimatedNavController()
            val homeViewModel: HomeViewModel = viewModel()
            val mostListenedViewModel: MostListenedViewModel = viewModel()

            deepLinkData.let {
                if (it?.getQueryParameter("token") != null) {
                    GlobalScope.launch(context = Dispatchers.Main) {
                        val sR = AuthEndpoint().getSession(it.getQueryParameter("token")!!)
                        val sessionKey = stringPreferencesKey("session_key")
                        dataStore.edit { userData ->
                            userData[sessionKey] = sR.session.key
                        }
                        navController.navigate("home")
                    }
                }
            }
            MusicorumMobileTheme {
                Surface(Modifier.fillMaxSize()) {
                    AnimatedNavHost(
                        navController = navController,
                        startDestination = "preload",
                        enterTransition = { slideInHorizontally(tween(800)) { fullWidth -> fullWidth } },
                        exitTransition = { slideOutHorizontally(tween(800)) { fullWidth -> -fullWidth / 2 } },
                        popExitTransition = { slideOutHorizontally(tween(800)) { fullWidth -> fullWidth / 2 } },
                        popEnterTransition = { slideInHorizontally(tween(800)) { fullWidth -> -fullWidth } }
                    ) {
                        composable("preload") {
                            Preload(
                                sharedPref = sharedPref,
                                nav = navController
                            )
                        }
                        composable("login") { Login() }
                        composable("home") {
                            Home(
                                homeViewModel = homeViewModel,
                                nav = navController
                            )
                        }
                        composable("recentScrobbles") {
                            RecentScrobbles(homeViewModel = homeViewModel, nav = navController)
                        }
                        composable("mostListened") {
                            MostListened(
                                homeViewModel = homeViewModel,
                                mostListenedViewModel = mostListenedViewModel,
                                nav = navController
                            )
                        }
                        composable("discover") { Discover(nav = navController) }
                        composable("scrobbling") { Scrobbling(nav = navController) }
                        composable("charts") { Charts(nav = navController) }
                        composable("account") { Account(nav = navController) }

                        composable(
                            "track/{trackData}",
                            arguments = listOf(navArgument("trackData") {
                                type = NavType.StringType
                            })
                        ) {
                            Track(
                                it.arguments?.getString("trackData"),
                                homeViewModel = homeViewModel,
                                nav = navController
                            )
                        }
                    }
                }
            }
        }
    }
}