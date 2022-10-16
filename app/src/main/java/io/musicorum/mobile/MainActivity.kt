package io.musicorum.mobile

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import io.musicorum.mobile.ktor.endpoints.AuthEndpoint
import io.musicorum.mobile.screens.Discover
import io.musicorum.mobile.screens.Home
import io.musicorum.mobile.screens.Login
import io.musicorum.mobile.screens.Preload
import io.musicorum.mobile.ui.theme.MusicorumMobileTheme
import io.musicorum.mobile.viewmodels.UserViewModel
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class, DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deepLinkData: Uri? = intent.data
        val sharedPref = this.getPreferences(MODE_PRIVATE)

        Log.d("ENV", BuildConfig.LASTFM_API_KEY)

        setContent {
            val navController = rememberAnimatedNavController()
            val userViewModel: UserViewModel = viewModel()

            deepLinkData.let {
                if (it?.getQueryParameter("token") != null) {
                    GlobalScope.launch(context = Dispatchers.Main) {
                        val sR = AuthEndpoint().getSession(it.getQueryParameter("token")!!)
                        with(sharedPref.edit()) {
                            putString("l_key", sR.session.key)
                            apply()
                        }
                        navController.navigate("home")
                    }
                }
            }

            MusicorumMobileTheme {
                AnimatedNavHost(navController = navController, startDestination = "preload") {
                    composable("preload") {
                        Preload(sharedPref = sharedPref, nav = navController)
                    }
                    composable("login", enterTransition = { slideInVertically() }) {
                        Login()

                    }
                    composable("home", enterTransition = { slideInVertically() }) {
                        Home(
                            nav = navController,
                            userViewModel = userViewModel,
                            sharedPref = sharedPref
                        )
                    }
                    composable("discover") {
                        Discover(nav = navController)
                    }
                }
            }
        }
    }
}