package io.musicorum.mobile.views.settings

import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import io.musicorum.mobile.R
import io.musicorum.mobile.components.MusicorumTopBar
import io.musicorum.mobile.services.NotificationListener
import io.musicorum.mobile.ui.theme.ContentSecondary
import io.musicorum.mobile.ui.theme.EvenLighterGray
import io.musicorum.mobile.ui.theme.LighterGray
import io.musicorum.mobile.ui.theme.MostlyRed
import io.musicorum.mobile.ui.theme.Success
import io.musicorum.mobile.ui.theme.Typography
import io.musicorum.mobile.viewmodels.ScrobbleSettingsVm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrobbleSettings(vm: ScrobbleSettingsVm = viewModel()) {
    val appBarState = rememberTopAppBarState(initialContentOffset = 700f)
    val appBarBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = appBarState)
    val ctx = LocalContext.current
    val styledSwitch =
        SwitchDefaults.colors(
            checkedTrackColor = MostlyRed,
            uncheckedThumbColor = ContentSecondary,
            uncheckedTrackColor = LighterGray,
            uncheckedBorderColor = EvenLighterGray
        )
    val lifecycle = LocalLifecycleOwner.current

    val scrobblePoint = vm.scrobblePoint.observeAsState(50f).value
    val enabled = vm.isEnabled.observeAsState(false).value
    val updatesNowPlaying = vm.updateNowPlaying.observeAsState(false).value
    val mediaApps = vm.availableApps.observeAsState(emptyList()).value
    val enabledPackageSet = vm.enabledPackageSet.observeAsState(emptySet()).value
    val showSpotifyModal = vm.showSpotifyModal.observeAsState(false)
    val hasPermission = vm.hasPermission.observeAsState(false).value

    val notificationIntent =
        Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").apply {
            val app = "${ctx.packageName}/${NotificationListener::class.java.name}"
            val fragmentKey = ":settings:fragment_args_key"
            val showFragmentKey = ":settings:show_fragment_args"
            putExtra(fragmentKey, app)
            putExtra(showFragmentKey, Bundle().apply { putString(fragmentKey, app) })
        }

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                vm.checkNotificationPermission()
            }
        }
        lifecycle.lifecycle.addObserver(observer)
        onDispose { lifecycle.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        vm.checkNotificationPermission()
    }

    if (showSpotifyModal.value) {
        SpotifyModal(
            onDismiss = { vm.showSpotifyModal.value = false },
            onEnable = { vm.enableSpotify() })
    }

    Scaffold(topBar = {
        MusicorumTopBar(
            text = "Scrobble Settings",
            scrollBehavior = appBarBehavior,
            fadeable = false
        ) {}
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Item(
                stringResource(R.string.enable_scrobbling),
                stringResource(R.string.device_scrobbling_description)
            ) {
                Switch(
                    checked = enabled ?: false,
                    onCheckedChange = { vm.updateScrobbling(it) },
                    colors = styledSwitch
                )
            }
            Item(
                stringResource(R.string.notification_access),
                stringResource(R.string.notification_permission_help_text),
                intent = notificationIntent
            ) {
                if (hasPermission == true) {
                    Icon(Icons.Rounded.Check, null, tint = Success)
                } else Icon(Icons.Rounded.Error, null, tint = Color.Red)
            }
            Item(
                stringResource(R.string.update_now_playing),
                stringResource(R.string.update_now_playing_description)
            ) {
                Switch(
                    checked = updatesNowPlaying ?: false,
                    onCheckedChange = { vm.updateNowPlaying(it) },
                    colors = styledSwitch
                )
            }
            Column {
                Item(
                    stringResource(R.string.scrobble_point),
                    stringResource(R.string.scrobble_point_description)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("30%", style = Typography.bodySmall, modifier = Modifier.alpha(.55f))
                    Text("90%", style = Typography.bodySmall, modifier = Modifier.alpha(.55f))
                }

                Slider(
                    value = scrobblePoint,
                    onValueChange = { vm.scrobblePoint.value = it },
                    valueRange = 30f..90f,
                    steps = 15,
                    onValueChangeFinished = { vm.updateScrobblePoint() }
                )
            }

            SectionTitle(sectionName = stringResource(R.string.media_apps), padding = false)
            Text(
                text = stringResource(R.string.media_apps_description),
                style = Typography.bodySmall,
                modifier = Modifier.alpha(.55f)
            )

            mediaApps.forEach { app ->
                ListItem(
                    headlineContent = { Text(text = app.displayName) },
                    leadingContent = {
                        Image(
                            app.logo.toBitmap().asImageBitmap(),
                            null,
                            modifier = Modifier.size(30.dp)
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = app.packageName in (enabledPackageSet ?: emptyList()),
                            onCheckedChange = { vm.updateMediaApp(app.packageName, it) },
                            colors = styledSwitch
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun Item(
    primaryText: String,
    supportingText: String? = null,
    intent: Intent? = null,
    trail: (@Composable () -> Unit)? = null,
) {
    val ctx = LocalContext.current
    val source = remember { MutableInteractionSource() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .then(
                if (intent != null) {
                    Modifier.clickable(
                        enabled = true,
                        indication = null,
                        interactionSource = source
                    ) { ctx.startActivity(intent) }
                } else Modifier
            )
    ) {
        Column {
            Text(primaryText, style = Typography.bodyLarge)
            supportingText?.let {
                Text(
                    it,
                    style = Typography.bodySmall,
                    modifier = Modifier
                        .alpha(.55f)
                        .widthIn(0.dp, if (trail == null) 400.dp else 270.dp)
                )
            }
        }
        trail?.let {
            it()
        }
    }
}

@Composable
fun SpotifyModal(onEnable: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Image(painterResource(R.drawable.spotify_icon), null) },
        title = { Text(stringResource(R.string.enable_scrobbling_for_spotify)) },
        text = { Text(stringResource(R.string.spotify_scrobble_description)) },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(stringResource(R.string.dismiss))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
                onEnable()
            }) {
                Text(stringResource(R.string.enable))
            }
        },
        containerColor = LighterGray
    )
}
