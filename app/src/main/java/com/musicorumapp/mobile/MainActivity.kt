package com.musicorumapp.mobile

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.musicorumapp.mobile.authentication.AuthenticationPreferences
import com.musicorumapp.mobile.states.LocalAuth
import com.musicorumapp.mobile.states.LocalAuthContent
import com.musicorumapp.mobile.states.LocalSnackbarContext
import com.musicorumapp.mobile.states.LocalSnackbarContextContent
import com.musicorumapp.mobile.states.models.AuthenticationValidationState
import com.musicorumapp.mobile.states.models.AuthenticationViewModel
import com.musicorumapp.mobile.ui.LoginScreen
import com.musicorumapp.mobile.ui.navigation.BottomNavigationBar
import com.musicorumapp.mobile.ui.navigation.MainNavigationHost
import com.musicorumapp.mobile.ui.theme.MusicorumTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences(Constants.AUTH_PREFS_KEY, MODE_PRIVATE)
        val authPrefs = AuthenticationPreferences(prefs)

        var gotToken: String? = null

        if (
            !authPrefs.checkIfTokenExists()
            && intent != null
            && intent.data != null
            && intent.action == "android.intent.action.VIEW"
        ) {
            val token = intent.data?.getQueryParameter("token")
            if (token != null) {
                Log.i(Constants.LOG_TAG, "token: $token")

                gotToken = token
            }
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setTheme(R.style.Theme_Musicorum_NoActionBar)

        setContent {
            MusicorumTheme {
                ProvideWindowInsets {
                    AuthSwitcher(
                        token = gotToken,
                        authPrefs = authPrefs
                    )
                }
            }
        }
    }
}


@Composable
fun AuthSwitcher(
    authPrefs: AuthenticationPreferences,
    token: String?,
) {
    val authenticationViewModel: AuthenticationViewModel = hiltViewModel()
    val scaffoldState = rememberScaffoldState()
    val snackbarCoroutineScope = rememberCoroutineScope()


    fun showSnackBar(content: String) {
        snackbarCoroutineScope.launch {
            scaffoldState.snackbarHostState.showSnackbar(content)
        }
    }

    val validationState: Int by authenticationViewModel.authenticationValidationState.observeAsState(
        AuthenticationValidationState.NONE
    )

    if (validationState == AuthenticationValidationState.NONE) {
        when {
            authPrefs.checkIfTokenExists() -> {
                authenticationViewModel.fetchUser {
                    showSnackBar(it)
                }
            }
            token != null -> {
                authenticationViewModel.authenticateFromToken(token) { showSnackBar(it) }
            }
            else -> authenticationViewModel.setAuthenticationValidationState(AuthenticationValidationState.LOGGED_OUT)
        }

    }

    Log.i(Constants.LOG_TAG, validationState.toString())
    Log.i(Constants.LOG_TAG, "token: " + authPrefs.getLastfmSessionToken())

    val navController = rememberNavController()

    CompositionLocalProvider(
        LocalAuth provides LocalAuthContent(authenticationViewModel.user.value),
        LocalSnackbarContext provides LocalSnackbarContextContent(scaffoldState.snackbarHostState)
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            bottomBar = {
                if (
                    validationState == AuthenticationValidationState.AUTHENTICATING
                    || validationState == AuthenticationValidationState.LOGGED_IN
                )
                    BottomNavigationBar(navController = navController)
            }
        ) {

            Box(
                modifier = Modifier
                    .navigationBarsWithImePadding()
                    .padding(bottom = 56.dp)
            ) {
                if (
                    validationState == AuthenticationValidationState.AUTHENTICATING
                    || validationState == AuthenticationValidationState.LOGGED_IN
                )
                    MainApp(authenticationViewModel, authPrefs, navController)
                else
                    LoginScreen(authenticationViewModel, authPrefs)
            }

        }
    }
}

@Composable
fun MainApp(
    authenticationViewModel: AuthenticationViewModel?,
    authPrefs: AuthenticationPreferences?,
    navController: NavHostController
) {
    val token = authPrefs?.getLastfmSessionToken() ?: "."
    val systemUiController = rememberSystemUiController()

    systemUiController.setNavigationBarColor(
        Color.Transparent,
        navigationBarContrastEnforced = false
    )

    MainNavigationHost(
        navController = navController,
        authenticationViewModel = authenticationViewModel,
    )
}