package io.musicorum.mobile.screens

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Preload(sharedPref: SharedPreferences, nav: NavHostController) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator()
        }

        LaunchedEffect(key1 = "1") {
            launch {
                val userKey = sharedPref.getString("l_key", null)
                if (userKey == null) {
                    Log.i("Preload", "Missing token - redirecting to login")
                    nav.navigate("login") { launchSingleTop = true }
                } else {
                    Log.i("Preload", "Token present - navigating home")
                    delay(2)
                    nav.navigate("home") { launchSingleTop = true }
                }
            }
        }
    }
}