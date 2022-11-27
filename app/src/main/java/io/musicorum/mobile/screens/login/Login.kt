package io.musicorum.mobile.screens.login

import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.musicorum.mobile.BuildConfig
import io.musicorum.mobile.MutableUserState
import io.musicorum.mobile.R
import io.musicorum.mobile.ui.theme.KindaBlack
import io.musicorum.mobile.utils.handleAuth

@Composable
fun Login(nav: NavController, deepLinkToken: String?) {
    val customTabsIntent = CustomTabsIntent.Builder()
        .build()
    val ctx = LocalContext.current
    val authorizationURL =
        "http://www.last.fm/api/auth/?api_key=${BuildConfig.LASTFM_API_KEY}&cb=musicorum://auth-callback"
    val loading = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = deepLinkToken) {
        if (deepLinkToken != null) {
            Log.d("Login", "Received session token $deepLinkToken")
            loading.value = true
            handleAuth(deepLinkToken) { user, sk ->
                if (user != null) {
                    MutableUserState.value = user
                    nav.navigate("user_confirmation/${sk}")
                }
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .background(KindaBlack)
            .statusBarsPadding()
    ) {
        Text(
            text = stringResource(R.string.homescreen_welcome),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = stringResource(R.string.homescreen_lfm_access),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(25.dp))
        Button(
            onClick = {
                customTabsIntent.launchUrl(ctx, Uri.parse(authorizationURL))
            },
            enabled = !loading.value
        ) {
            AnimatedVisibility(visible = loading.value) {
                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
            }
            Text(
                text = stringResource(R.string.homescreen_login_button),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = if (loading.value) 15.dp else 0.dp)
            )

        }
    }
}