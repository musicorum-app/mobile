package io.musicorum.mobile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import io.musicorum.mobile.components.BottomNavBar
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.screens.*
import io.musicorum.mobile.screens.individual.Album
import io.musicorum.mobile.screens.individual.Artist
import io.musicorum.mobile.screens.individual.Track
import io.musicorum.mobile.screens.individual.User
import io.musicorum.mobile.screens.login.loginGraph
import io.musicorum.mobile.serialization.User
import io.musicorum.mobile.ui.theme.AlmostBlack
import io.musicorum.mobile.ui.theme.MusicorumMobileTheme
import io.musicorum.mobile.utils.LocalSnackbar
import io.musicorum.mobile.utils.LocalSnackbarContext
import io.musicorum.mobile.viewmodels.HomeViewModel
import io.musicorum.mobile.viewmodels.MostListenedViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "userdata")
val LocalUser = compositionLocalOf<User?> { null }
val MutableUserState = mutableStateOf<User?>(null)

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController

    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )


        setContent {
            navController = rememberAnimatedNavController()
            val homeViewModel: HomeViewModel = viewModel()
            val mostListenedViewModel: MostListenedViewModel = viewModel()
            val ctx = LocalContext.current
            val snackHostState = remember { SnackbarHostState() }
            val systemUiController = rememberSystemUiController()

            if (intent?.data == null) {
                if (MutableUserState.value == null) {
                    LaunchedEffect(Unit) {
                        val sessionKey = ctx.applicationContext.dataStore.data.map { prefs ->
                            prefs[stringPreferencesKey("session_key")]
                        }.firstOrNull()
                        if (sessionKey == null) {
                            navController.navigate("login")
                        } else {
                            MutableUserState.value = UserEndpoint().getSessionUser(sessionKey)
                        }
                    }
                }
            }

            SideEffect {
                systemUiController.setNavigationBarColor(Color.Transparent)
                systemUiController.setStatusBarColor(AlmostBlack)
            }

            val showNav =
                when (navController.currentBackStackEntryAsState().value?.destination?.route) {
                    "home" -> true
                    "scrobbling" -> true
                    "profile" -> true
                    else -> false
                }

            CompositionLocalProvider(
                LocalUser provides MutableUserState.value,
                LocalSnackbar provides LocalSnackbarContext(snackHostState)
            ) {
                MusicorumMobileTheme {
                    Scaffold(
                        bottomBar = {
                            AnimatedVisibility(
                                visible = showNav,
                                enter = slideInVertically(initialOffsetY = { it }),
                                exit = slideOutVertically(targetOffsetY = { it }),
                            ) {
                                BottomNavBar(nav = navController)
                            }
                        },
                        snackbarHost = { SnackbarHost(hostState = snackHostState) }
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
                                loginGraph(navController = navController)

                                composable("home") {
                                    Home(
                                        homeViewModel = homeViewModel,
                                        nav = navController
                                    )
                                }

                                composable("recentScrobbles") {
                                    RecentScrobbles(nav = navController)
                                }

                                composable("mostListened") {
                                    MostListened(
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

                                composable(
                                    "artist/{artistName}",
                                    arguments = listOf(navArgument("artistName") {
                                        type = NavType.StringType
                                    })
                                ) {
                                    Artist(
                                        artistName = it.arguments?.getString("artistName")!!,

                                        )
                                }

                                composable(
                                    "album/{albumData}",
                                    arguments = listOf(navArgument("albumData") {
                                        type = NavType.StringType
                                    }),
                                ) {
                                    Album(it.arguments?.getString("albumData"), nav = navController)
                                }

                                composable("discover") { Discover() }
                                composable("scrobbling") { Scrobbling() }
                                composable("charts") { Charts() }
                                composable("profile") { Account(navController) }

                                composable(
                                    "track/{trackData}",
                                    arguments = listOf(navArgument("trackData") {
                                        type = NavType.StringType
                                    })
                                ) {
                                    Track(
                                        it.arguments?.getString("trackData"),
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.data?.let {
            navController.navigate(it)
        }
    }
}