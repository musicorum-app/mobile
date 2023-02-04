package io.musicorum.mobile.screens.settings

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import io.musicorum.mobile.R
import io.musicorum.mobile.components.MusicorumTopBar
import io.musicorum.mobile.scrobblePrefs
import io.musicorum.mobile.services.NotificationListener
import io.musicorum.mobile.ui.theme.ContentSecondary
import io.musicorum.mobile.ui.theme.EvenLighterGray
import io.musicorum.mobile.ui.theme.LighterGray
import io.musicorum.mobile.ui.theme.MostlyRed
import io.musicorum.mobile.ui.theme.Success
import io.musicorum.mobile.ui.theme.Typography
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrobbleSettings() {
    val appBarState = rememberTopAppBarState(initialContentOffset = 700f)
    val appBarBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = appBarState)
    val coroutine = rememberCoroutineScope()
    val ctx = LocalContext.current
    val styledSwitch =
        SwitchDefaults.colors(
            checkedTrackColor = MostlyRed,
            uncheckedThumbColor = ContentSecondary,
            uncheckedTrackColor = LighterGray,
            uncheckedBorderColor = EvenLighterGray
        )
    val scrobblePointKey = floatPreferencesKey("scrobblePoint")
    val enabledKey = booleanPreferencesKey("enabled")
    // val newAppsKey = booleanPreferencesKey("newApps")
    val enabledAppsKey = stringSetPreferencesKey("enabledApps")
    val updateNowPlayingKey = booleanPreferencesKey("updateNowPlaying")
    val hasNotificationPermission =
        NotificationManagerCompat.getEnabledListenerPackages(ctx).contains(ctx.packageName)
    val pm = ctx.packageManager

    val scrobblePointData = runBlocking {
        ctx.scrobblePrefs.data.map { p -> p[scrobblePointKey] }.first()
    }
    val enabledData = runBlocking {
        ctx.scrobblePrefs.data.map { p -> p[enabledKey] }.first()
    }
    /* val newAppsData = runBlocking {
         ctx.scrobblePrefs.data.map { p -> p[newAppsKey] }.first()
     }*/
    val enabledAppsData = runBlocking {
        ctx.scrobblePrefs.data.map { p -> p[enabledAppsKey] }.first()
    }
    val updateNowPlayingData = runBlocking {
        ctx.scrobblePrefs.data.map { p -> p[updateNowPlayingKey] }.first()
    }

    if (scrobblePointData == null) {
        LaunchedEffect(key1 = Unit) {
            ctx.scrobblePrefs.edit { p ->
                p[scrobblePointKey] = 50f
            }
        }
    }

    val scrobblePoint = remember { mutableStateOf(scrobblePointData ?: 50f) }
    val enabled = remember { mutableStateOf(enabledData ?: false) }
    val updatesNowPlaying = remember { mutableStateOf(updateNowPlayingData ?: false) }
    // val newApps = remember { mutableStateOf(newAppsData ?: false) }
    val enabledApps = remember { enabledAppsData?.toMutableStateList() ?: mutableStateListOf() }
    val showSpotifyModal = remember { mutableStateOf(false) }
    val mediaApps: MutableList<Triple<Drawable, String, String>> = mutableListOf()

    pm.getInstalledApplications(0).forEach { appInfo ->
        if (appInfo.category >= ApplicationInfo.CATEGORY_AUDIO) {
            val appLogo = pm.getApplicationIcon(appInfo)
            val appName = pm.getApplicationLabel(appInfo)
            mediaApps.add(Triple(appLogo, appName.toString(), appInfo.packageName))
        }
    }

    val notificationIntent =
        Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").apply {
            val app = "${ctx.packageName}/${NotificationListener::class.java.name}"
            val fragmentKey = ":settings:fragment_args_key"
            val showFragmentKey = ":settings:show_fragment_args"
            putExtra(fragmentKey, app)
            putExtra(showFragmentKey, Bundle().apply { putString(fragmentKey, app) })
        }

    AnimatedVisibility(showSpotifyModal.value) {
        SpotifyModal(state = showSpotifyModal) {
            enabledApps.add("com.spotify.music")
            coroutine.launch {
                ctx.scrobblePrefs.edit { p ->
                    p[enabledAppsKey] = enabledApps.toSet()
                }
            }
        }
    }

    Scaffold(topBar = {
        MusicorumTopBar(
            text = stringResource(id = R.string.settings),
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
                    checked = enabled.value,
                    onCheckedChange = {
                        enabled.value = it
                        if (it) {
                            Firebase.analytics.logEvent("enable_device_scrobbling", null)
                        } else {
                            Firebase.analytics.logEvent("disable_device_scrobbling", null)
                        }
                        coroutine.launch {
                            ctx.scrobblePrefs.edit { p ->
                                p[enabledKey] = it
                            }
                        }
                    },
                    colors = styledSwitch
                )
            }
            Item(
                stringResource(R.string.notification_access),
                stringResource(R.string.notification_permission_help_text),
                intent = notificationIntent
            ) {
                if (hasNotificationPermission) {
                    Icon(Icons.Rounded.Check, null, tint = Success)
                } else Icon(Icons.Rounded.Error, null, tint = Color.Red)
            }
            Item(
                stringResource(R.string.update_now_playing),
                stringResource(R.string.update_now_playing_description)
            ) {
                Switch(
                    checked = updatesNowPlaying.value,
                    onCheckedChange = {
                        updatesNowPlaying.value = it
                        coroutine.launch {
                            ctx.scrobblePrefs.edit { p ->
                                p[updateNowPlayingKey] = it
                            }
                        }
                    },
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
                    value = scrobblePoint.value,
                    onValueChange = { scrobblePoint.value = it },
                    valueRange = 30f..90f,
                    steps = 15,
                    onValueChangeFinished = {
                        coroutine.launch {
                            ctx.scrobblePrefs.edit { p ->
                                p[scrobblePointKey] = scrobblePoint.value
                            }
                        }
                    }
                )
            }
            /*            Item("Enable scrobbling for new apps") {
                            Switch(
                                checked = newApps.value,
                                onCheckedChange = {
                                    newApps.value = it
                                    coroutine.launch {
                                        ctx.scrobblePrefs.edit { p ->
                                            p[newAppsKey] = it
                                        }
                                    }
                                },
                                colors = styledSwitch
                            )
                        }*/
            SectionTitle(sectionName = stringResource(R.string.media_apps))
            Text(
                text = stringResource(R.string.media_apps_description),
                style = Typography.bodySmall,
                modifier = Modifier.alpha(.55f)
            )

            // Triple follows the following order: App icon, app name and package
            mediaApps.forEach { app ->
                MediaApp(
                    name = app.second,
                    icon = app.first,
                    enabled = app.third in enabledApps,
                    onChange = {
                        if (app.second == "Spotify" && it == true) {
                            showSpotifyModal.value = true
                            return@MediaApp
                        }
                        enabledApps.remove(app.third) || enabledApps.add(app.third)
                        coroutine.launch {
                            ctx.scrobblePrefs.edit { p ->
                                p[enabledAppsKey] = enabledApps.toSet()
                            }
                        }
                    })
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

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .then(
                if (intent != null) {
                    Modifier.clickable {
                        ctx.startActivity(intent)
                    }
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
fun MediaApp(name: String, icon: Drawable, enabled: Boolean, onChange: (Boolean?) -> Unit) {
    val styledSwitch =
        SwitchDefaults.colors(
            checkedTrackColor = MostlyRed,
            uncheckedThumbColor = ContentSecondary,
            uncheckedTrackColor = LighterGray,
            uncheckedBorderColor = EvenLighterGray
        )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(icon.toBitmap().asImageBitmap(), null, modifier = Modifier.size(25.dp))
            Text(text = name)
        }
        Switch(checked = enabled, onCheckedChange = onChange, colors = styledSwitch)
    }
}

@Composable
fun SpotifyModal(state: MutableState<Boolean>, onEnable: () -> Unit) {
    AlertDialog(
        onDismissRequest = { state.value = false },
        icon = { Image(painterResource(R.drawable.spotify_icon), null) },
        title = { Text(stringResource(R.string.enable_scrobbling_for_spotify)) },
        text = { Text(stringResource(R.string.spotify_scrobble_description)) },
        dismissButton = {
            TextButton(onClick = { state.value = false }) {
                Text(stringResource(R.string.dismiss))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                state.value = false.also { onEnable() }
            }) {
                Text(stringResource(R.string.enable))
            }
        },
        containerColor = LighterGray
    )
}
