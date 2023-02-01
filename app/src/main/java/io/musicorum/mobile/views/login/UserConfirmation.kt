package io.musicorum.mobile.views.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import io.musicorum.mobile.BuildConfig
import io.musicorum.mobile.LocalUser
import io.musicorum.mobile.MutableUserState
import io.musicorum.mobile.R
import io.musicorum.mobile.coil.PlaceholderType
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.ui.theme.Heading2
import io.musicorum.mobile.ui.theme.KindaBlack
import io.musicorum.mobile.ui.theme.Typography
import io.musicorum.mobile.utils.commitUser
import kotlinx.coroutines.launch

@Composable
fun UserConfirmation(nav: NavController, sessionKey: String) {
    Column(
        modifier = Modifier
            .background(KindaBlack)
            .padding(horizontal = 20.dp)
            .fillMaxSize()
            .statusBarsPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val checkboxChecked = remember { mutableStateOf(true) }
        val dialogOpened = remember { mutableStateOf(false) }
        val user = LocalUser.current
        val ctx = LocalContext.current
        val coroutine = rememberCoroutineScope()
        user?.let { user1 ->
            if (dialogOpened.value) {
                AnalyticsDialog(open = dialogOpened, checkBoxState = checkboxChecked)
            }

            AsyncImage(
                model = defaultImageRequestBuilder(
                    url = user1.user.bestImageUrl,
                    PlaceholderType.USER
                ), contentDescription = null,
                modifier = Modifier.clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(25.dp))
            Text(text = stringResource(id = R.string.welcome, user1.user.name), style = Heading2)
            Spacer(modifier = Modifier.width(25.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = checkboxChecked.value,
                    onCheckedChange = {
                        if (checkboxChecked.value) {
                            dialogOpened.value = true
                        } else {
                            checkboxChecked.value = true
                        }
                    }
                )
                Text(
                    text = stringResource(id = R.string.analytics_description),
                    style = Typography.bodyMedium,
                    modifier = Modifier.alpha(0.55f)
                )
            }
            Spacer(modifier = Modifier.width(25.dp))
            Button(onClick = {
                coroutine.launch {
                    if (!BuildConfig.DEBUG) {
                        Firebase.crashlytics.setCrashlyticsCollectionEnabled(checkboxChecked.value)
                        Firebase.analytics.setAnalyticsCollectionEnabled(checkboxChecked.value)
                    }
                    commitUser(sessionKey, ctx)
                    val sessionUser = UserEndpoint.getSessionUser(sessionKey)
                    sessionUser?.let {
                        MutableUserState.value = it
                        nav.navigate("deviceScrobble")
                    }
                }
            }) {
                Text(text = stringResource(id = R.string.confirmation_continue))
            }
        }
    }
}


@Composable
private fun AnalyticsDialog(open: MutableState<Boolean>, checkBoxState: MutableState<Boolean>) {
    AlertDialog(
        onDismissRequest = { open.value = false },
        title = { Text(text = stringResource(id = R.string.hold_on)) },
        confirmButton = {
            TextButton(onClick = { checkBoxState.value = false; open.value = false }) {
                Text(text = stringResource(id = R.string.disable_anyway))
            }
        },
        dismissButton = {
            TextButton(onClick = { open.value = false }) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        text = {
            Text(text = stringResource(id = R.string.analytics_warning))
        }
    )
}