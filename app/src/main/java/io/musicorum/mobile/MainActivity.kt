package io.musicorum.mobile

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.crowdin.platform.Crowdin
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.common.ConnectionResult.SERVICE_MISSING
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import dagger.hilt.android.AndroidEntryPoint
import io.musicorum.mobile.components.BottomNavBar
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.screens.Account
import io.musicorum.mobile.screens.Charts
import io.musicorum.mobile.screens.Discover
import io.musicorum.mobile.screens.Home
import io.musicorum.mobile.screens.MostListened
import io.musicorum.mobile.screens.RecentScrobbles
import io.musicorum.mobile.screens.Scrobbling
import io.musicorum.mobile.screens.individual.Album
import io.musicorum.mobile.screens.individual.AlbumTracklist
import io.musicorum.mobile.screens.individual.Artist
import io.musicorum.mobile.screens.individual.PartialAlbum
import io.musicorum.mobile.screens.individual.Track
import io.musicorum.mobile.screens.individual.User
import io.musicorum.mobile.screens.login.loginGraph
import io.musicorum.mobile.screens.settings.ScrobbleSettings
import io.musicorum.mobile.screens.settings.Settings
import io.musicorum.mobile.serialization.User
import io.musicorum.mobile.ui.theme.KindaBlack
import io.musicorum.mobile.ui.theme.MusicorumMobileTheme
import io.musicorum.mobile.utils.CrowdinUtils
import io.musicorum.mobile.utils.LocalSnackbar
import io.musicorum.mobile.utils.LocalSnackbarContext
import io.musicorum.mobile.utils.MessagingService
import io.musicorum.mobile.viewmodels.MostListenedViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

val Context.userData: DataStore<Preferences> by preferencesDataStore(name = "userdata")
val Context.scrobblePrefs by preferencesDataStore(name = "scrobblePrefs")
val LocalUser = compositionLocalOf<User?> { null }
val LocalNavigation = compositionLocalOf<NavHostController?> { null }
val MutableUserState = mutableStateOf<User?>(null)
val LocalAnalytics = compositionLocalOf<FirebaseAnalytics?> { null }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(Crowdin.wrapContext(newBase))
    }

    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) {
            Crowdin.registerShakeDetector(this)
        }

        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 10
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("remote config", "update success")
            }
        }
        Log.d("remove config", remoteConfig.all.toString())


        CrowdinUtils.initCrowdin(applicationContext)
        askNotificationPermission()
        MessagingService.createNotificationChannel(applicationContext)

        if (GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(this) == SERVICE_MISSING
        ) {
            GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)
        }

        WindowCompat.setDecorFitsSystemWindows(window, true)

        firebaseAnalytics = Firebase.analytics
        /*        window.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )

                WindowCompat.setDecorFitsSystemWindows(window, false)*/

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FCM", "fcm token: ${task.result}")
            }
        }

        setContent {
            val useDarkIcons = !isSystemInDarkTheme()
            navController = rememberAnimatedNavController()

            val mostListenedViewModel: MostListenedViewModel = viewModel()
            val ctx = LocalContext.current
            val snackHostState = remember { SnackbarHostState() }
            val systemUiController = rememberSystemUiController()

            if (intent?.data == null) {
                if (MutableUserState.value == null) {
                    LaunchedEffect(Unit) {
                        val sessionKey = ctx.applicationContext.userData.data.map { prefs ->
                            prefs[stringPreferencesKey("session_key")]
                        }.firstOrNull()
                        if (sessionKey == null) {
                            navController.navigate("login")
                        } else {
                            MutableUserState.value = UserEndpoint.getSessionUser(sessionKey)
                        }
                    }
                }
            }

            DisposableEffect(systemUiController, useDarkIcons) {
                systemUiController.setNavigationBarColor(Color.Transparent)
                systemUiController.setSystemBarsColor(KindaBlack)

                onDispose { }
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
                LocalSnackbar provides LocalSnackbarContext(snackHostState),
                LocalNavigation provides navController,
                LocalAnalytics provides firebaseAnalytics
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
                                enterTransition = { slideInHorizontally(tween(500)) { fullWidth -> fullWidth } },
                                exitTransition = { slideOutHorizontally(tween(500)) { fullWidth -> -fullWidth / 2 } },
                                popExitTransition = { slideOutHorizontally(tween(500)) { fullWidth -> fullWidth / 2 } },
                                popEnterTransition = { slideInHorizontally(tween(500)) { fullWidth -> -fullWidth } }
                            ) {
                                loginGraph(navController = navController)

                                composable("home") { Home() }

                                composable("recentScrobbles") { RecentScrobbles() }

                                composable("mostListened") {
                                    MostListened(mostListenedViewModel = mostListenedViewModel)
                                }

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
                                composable("profile") { Account() }

                                composable(
                                    "track/{trackData}",
                                    arguments = listOf(navArgument("trackData") {
                                        type = NavType.StringType
                                    })
                                ) {
                                    Track(it.arguments?.getString("trackData"))
                                }

                                composable(
                                    "albumTracklist/{albumData}",
                                    arguments = listOf(navArgument("albumData") {
                                        type = NavType.StringType
                                    })
                                ) {
                                    val partialAlbum = Json.decodeFromString<PartialAlbum>(
                                        it.arguments!!.getString("albumData")!!
                                    )
                                    AlbumTracklist(partialAlbum = partialAlbum)
                                }

                                composable("settings") { Settings() }
                                composable("settings/scrobble") { ScrobbleSettings() }
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
