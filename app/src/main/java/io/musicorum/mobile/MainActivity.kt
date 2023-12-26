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
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
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
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.crowdin.platform.Crowdin
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.common.ConnectionResult.SERVICE_MISSING
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import dagger.hilt.android.AndroidEntryPoint
import io.musicorum.mobile.datastore.ScrobblePreferences
import io.musicorum.mobile.datastore.UserData
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.models.FetchPeriod
import io.musicorum.mobile.repositories.LocalUserRepository
import io.musicorum.mobile.router.BottomNavBar
import io.musicorum.mobile.ui.theme.KindaBlack
import io.musicorum.mobile.ui.theme.MusicorumMobileTheme
import io.musicorum.mobile.utils.CrowdinUtils
import io.musicorum.mobile.utils.LocalSnackbar
import io.musicorum.mobile.utils.LocalSnackbarContext
import io.musicorum.mobile.utils.MessagingService
import io.musicorum.mobile.views.Account
import io.musicorum.mobile.views.Discover
import io.musicorum.mobile.views.RecentScrobbles
import io.musicorum.mobile.views.charts.BaseChartDetail
import io.musicorum.mobile.views.charts.Charts
import io.musicorum.mobile.views.collage.Collage
import io.musicorum.mobile.views.home.Home
import io.musicorum.mobile.views.individual.Album
import io.musicorum.mobile.views.individual.AlbumTracklist
import io.musicorum.mobile.views.individual.Artist
import io.musicorum.mobile.views.individual.PartialAlbum
import io.musicorum.mobile.views.individual.TagScreen
import io.musicorum.mobile.views.individual.Track
import io.musicorum.mobile.views.individual.User
import io.musicorum.mobile.views.login.loginGraph
import io.musicorum.mobile.views.mostListened.MostListened
import io.musicorum.mobile.views.scrobbling.Scrobbling
import io.musicorum.mobile.views.settings.PendingScrobbles
import io.musicorum.mobile.views.settings.ScrobbleSettings
import io.musicorum.mobile.views.settings.Settings
import io.sentry.android.core.SentryAndroid
import io.sentry.compose.withSentryObservableEffect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

val Context.userData: DataStore<Preferences> by preferencesDataStore(UserData.DataStoreName)
val Context.scrobblePrefs by preferencesDataStore(ScrobblePreferences.DataStoreName)
val LocalNavigation = compositionLocalOf<NavHostController?> { null }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

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

        val appUpdateManager = AppUpdateManagerFactory.create(this.applicationContext)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { info ->
            if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    IMMEDIATE,
                    this,
                    145
                )
                return@addOnSuccessListener
            }

            if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && info.updatePriority() >= 4
                && info.isUpdateTypeAllowed(IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    IMMEDIATE,
                    this,
                    145
                )
            }
        }

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
        /*window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )*/

        //WindowCompat.setDecorFitsSystemWindows(window, false)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FCM", "fcm token: ${task.result}")
            }
        }
        if (!BuildConfig.DEBUG) {
            try {
                SentryAndroid.init(this) { opts ->
                    opts.isAnrEnabled
                }
            } catch (_: Exception) {
            }
        }

        setContent {
            val useDarkIcons = !isSystemInDarkTheme()
            navController = rememberNavController().withSentryObservableEffect()

            val ctx = LocalContext.current
            val snackHostState = remember { SnackbarHostState() }
            val systemUiController = rememberSystemUiController()

            if (intent?.data == null) {
                LaunchedEffect(Unit) {
                    val sessionKey = ctx.applicationContext.userData.data.map { prefs ->
                        prefs[stringPreferencesKey("session_key")]
                    }.firstOrNull()
                    if (sessionKey == null) {
                        navController.navigate("login") {
                            popUpTo("home") {
                                inclusive = true
                            }
                        }
                    } else {
                        val localUser = LocalUserRepository(applicationContext)
                        if (localUser.getUser().username.isEmpty()) {
                            val userReq = UserEndpoint.getSessionUser(sessionKey)
                            localUser.create(userReq?.user)
                        }
                    }
                }

            }

            DisposableEffect(systemUiController, useDarkIcons) {
                systemUiController.setNavigationBarColor(Color.Transparent)
                systemUiController.setSystemBarsColor(KindaBlack)

                onDispose { }
            }

            val bottomBarDestinations =
                listOf("home", "scrobbling", "profile", "charts", "discover")
            val showNav =
                navController.currentBackStackEntryAsState().value?.destination?.route in bottomBarDestinations


            CompositionLocalProvider(
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
                    ) { pv ->
                        Surface(
                            Modifier
                                .fillMaxSize()
                                .padding(pv)
                        ) {
                            val animationCurve = CubicBezierEasing(0.76f, 0f, 0.24f, 1f)
                            val newRoute = navController.currentBackStackEntry?.destination?.route
                            NavHost(
                                navController = navController,
                                startDestination = "home",
                                enterTransition = {
                                    if (newRoute in bottomBarDestinations) {
                                        fadeIn(tween(200))
                                    } else {
                                        slideInHorizontally(tween(350)) { fullWidth -> fullWidth }
                                    }
                                },
                                exitTransition = {
                                    if (newRoute in bottomBarDestinations) {
                                        fadeOut(tween(200))
                                    } else {
                                        slideOutHorizontally(tween(350)) { fullWidth -> -fullWidth / 2 }
                                    }
                                },
                                popExitTransition = {
                                    slideOutHorizontally(
                                        tween(
                                            500,
                                            easing = animationCurve
                                        )
                                    ) { fullWidth -> fullWidth }

                                },
                                popEnterTransition = {
                                    if (newRoute in bottomBarDestinations) {
                                        fadeIn(tween(200))
                                    } else {
                                        slideInHorizontally(
                                            tween(
                                                500,
                                                easing = animationCurve
                                            )
                                        )
                                    }
                                }
                            ) {
                                /* WIP val views =
                                    Reflections("io.musicorum.mobile")
                                        .getMethodsAnnotatedWith(Route::class.java)
                                Log.d("REGISTRY", "${views.size} routes were found.")
                                views.forEach { method ->
                                    val routeName = method.annotations[0].annotationClass.simpleName
                                        ?: return@forEach
                                    composable(route = routeName) { method.invoke(null) }
                                }*/

                                loginGraph(navController = navController)

                                composable("home") { Home() }

                                composable("recentScrobbles") { RecentScrobbles() }

                                composable(
                                    route = "collage?entity={entity}&period={period}",
                                    arguments = listOf(
                                        navArgument("entity") {
                                            type = NavType.StringType
                                            defaultValue = "artist"
                                        },
                                        navArgument("period") {
                                            type = NavType.StringType
                                            defaultValue = "7day"
                                        }
                                    )
                                ) {
                                    Collage()
                                }

                                composable(
                                    "charts/detail?index={index}&period={period}",
                                    arguments = listOf(
                                        navArgument("index") {
                                            type = NavType.IntType
                                            defaultValue = 1
                                        },
                                        navArgument("period") {
                                            type = NavType.StringType
                                            defaultValue = FetchPeriod.WEEK.value
                                        }
                                    )
                                ) { backStack ->
                                    BaseChartDetail(backStack.arguments?.getInt("index")!!)
                                }

                                composable("mostListened") {
                                    MostListened()
                                }

                                composable(
                                    "user/{usernameArg}",
                                    arguments = listOf(navArgument("usernameArg") {
                                        type = NavType.StringType
                                    })
                                ) {
                                    User()
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
                                    arguments = listOf(
                                        navArgument("trackData") {
                                            type = NavType.StringType
                                        },
                                    )
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
                                composable("settings/pendingScrobbles") { PendingScrobbles() }

                                composable(
                                    "tag/{tagName}",
                                    arguments = listOf(
                                        navArgument("tagName") {
                                            type = NavType.StringType
                                        },
                                    )
                                ) {
                                    TagScreen()
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
