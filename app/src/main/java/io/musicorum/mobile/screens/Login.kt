package io.musicorum.mobile.screens

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.musicorum.mobile.BuildConfig

@Composable
fun Login() {
    val customTabsIntent = CustomTabsIntent.Builder()
        .build()
    val context = LocalContext.current
    val authorizationURL =
        "http://www.last.fm/api/auth/?api_key=${BuildConfig.LASTFM_API_KEY}&&cb=musicorum://auth-callback"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to the Musicorum app",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = "We need to access your Last.fm account to proceed",
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(25.dp))
        Button(onClick = {
            customTabsIntent.launchUrl(context, Uri.parse(authorizationURL))
        }) {
            Text(text = "LOG IN WITH LAST.FM", fontWeight = FontWeight.SemiBold)
        }
    }
}