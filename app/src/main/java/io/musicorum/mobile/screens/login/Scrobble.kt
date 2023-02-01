package io.musicorum.mobile.screens.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import io.musicorum.mobile.LocalNavigation
import io.musicorum.mobile.NotificationListener
import io.musicorum.mobile.R
import io.musicorum.mobile.ui.theme.KindaBlack
import io.musicorum.mobile.ui.theme.Typography

@Composable
fun Scrobble() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(20.dp)
            .background(KindaBlack)
    ) {
        val nav = LocalNavigation.current
        val ctx = LocalContext.current
        val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").apply {
            val app = "${ctx.packageName}/${NotificationListener::class.java.name}"
            val fragmentKey = ":settings:fragment_args_key"
            val showFragmentKey = ":settings:show_fragment_args"
            putExtra(fragmentKey, app)
            putExtra(showFragmentKey, Bundle().apply { putString(fragmentKey, app) })
        }
        val txtBtnColors = ButtonDefaults.textButtonColors(contentColor = Color.White)
        val lcOwner = rememberUpdatedState(newValue = LocalLifecycleOwner.current)
        val granted = rememberSaveable { mutableStateOf(false) }

        DisposableEffect(key1 = lcOwner) {
            val lifecycle = lcOwner.value.lifecycle
            val observer = LifecycleEventObserver { owner, event ->
                Log.d("scrobble screen", event.name)
                if (event == Lifecycle.Event.ON_RESUME) {
                    granted.value = NotificationManagerCompat.getEnabledListenerPackages(ctx)
                        .contains(ctx.packageName)
                }
            }

            lifecycle.addObserver(observer)
            onDispose { lifecycle.removeObserver(observer) }
        }

        if (granted.value) {
            nav?.navigate("home")
        }

        Image(painter = painterResource(id = R.drawable.device_with_player), null)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.grant_notification_access),
            style = Typography.displaySmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.grant_nofication_access_onboard_description),
            style = Typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = { ctx.startActivity(intent) }) {
            Text(text = stringResource(R.string.open_notification_settings))
        }
        TextButton(onClick = { nav?.navigate("home") }, colors = txtBtnColors) {
            Text(text = stringResource(R.string.notification_access_skip))
        }
    }
}