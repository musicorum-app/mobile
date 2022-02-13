package com.musicorumapp.mobile.ui

import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.musicorumapp.mobile.Constants
import com.musicorumapp.mobile.authentication.AuthenticationPreferences
import com.musicorumapp.mobile.states.models.AuthenticationViewModel
import com.musicorumapp.mobile.ui.theme.KindaBlack
import com.musicorumapp.mobile.ui.theme.MusicorumTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authenticationViewModel: AuthenticationViewModel?,
    authPrefs: AuthenticationPreferences?
) {
    val systemUiController = rememberSystemUiController()
    val context = LocalContext.current

    systemUiController.setNavigationBarColor(KindaBlack)
    Scaffold {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight(),
            ) {
                Text("Please login with your Last.fm account first")
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val colorSchemeParams = CustomTabColorSchemeParams.Builder()
                            .setToolbarColor(KindaBlack.toArgb())
                            .setNavigationBarColor(KindaBlack.toArgb())
                            .build()
                        val customTabsIntent = CustomTabsIntent.Builder()
                            .setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_DARK, colorSchemeParams)
                            .setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_LIGHT, colorSchemeParams)
                            .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
                            .build()
                        customTabsIntent.launchUrl(context, Uri.parse(Constants.MUSICORUM_LOGIN_URL))
                    },
                ) {
                    Text("Login with Last.fm")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreivew() {
    MusicorumTheme {
        LoginScreen(null, null)
    }
}