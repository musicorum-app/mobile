package io.musicorum.mobile.screens

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.NavHostController
import io.musicorum.mobile.R
import io.musicorum.mobile.dataStore
import io.musicorum.mobile.ui.theme.AlmostBlack
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Preload(sharedPref: SharedPreferences, nav: NavHostController) {
    val ctx = LocalContext.current
    LaunchedEffect(key1 = "1") {
        launch {
            val userKey = ctx.applicationContext.dataStore.data.map { prefs ->
                prefs[stringPreferencesKey("session_key")]
            }
            if (userKey.first() == null) {
                Log.i("Preload", "Missing token - redirecting to login")
                nav.navigate("login") { launchSingleTop = true }
            } else {
                Log.i("Preload", "Token present - navigating home")
                delay(2)
                nav.navigate("home") { launchSingleTop = true; restoreState = true }
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(AlmostBlack),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painterResource(id = R.drawable.icon_logo),
                contentDescription = "logo",
                modifier = Modifier.size(100.dp)
            )
            CircularProgressIndicator()
        }
    }

}