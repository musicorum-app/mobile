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
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import io.musicorum.mobile.R
import io.musicorum.mobile.coil.PlaceholderType
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.ui.theme.Heading2
import io.musicorum.mobile.ui.theme.KindaBlack
import io.musicorum.mobile.ui.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun UserConfirmation(
    viewModel: UserConfirmationViewModel = viewModel(),
    nav: NavController,
    sessionKey: String
) {
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
        val user by viewModel.user.observeAsState(null)
        val coroutine = rememberCoroutineScope()
        user?.let { user1 ->
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
                        checkboxChecked.value = it
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
                    viewModel.setAnalyticsPreferences(checkboxChecked.value)
                    viewModel.saveSession(sessionKey)
                    nav.navigate("deviceScrobble")
                }
            }) {
                Text(text = stringResource(id = R.string.confirmation_continue))
            }
        }
    }
}