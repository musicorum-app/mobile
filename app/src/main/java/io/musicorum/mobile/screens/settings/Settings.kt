package io.musicorum.mobile.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import coil.compose.AsyncImage
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import io.musicorum.mobile.BuildConfig
import io.musicorum.mobile.LocalNavigation
import io.musicorum.mobile.LocalUser
import io.musicorum.mobile.R
import io.musicorum.mobile.coil.PlaceholderType
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.scrobblePrefs
import io.musicorum.mobile.ui.theme.KindaBlack
import io.musicorum.mobile.ui.theme.LighterGray
import io.musicorum.mobile.ui.theme.MostlyRed
import io.musicorum.mobile.ui.theme.Typography
import io.musicorum.mobile.userData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Settings() {
    val user = LocalUser.current
    val ctx = LocalContext.current
    val nav = LocalNavigation.current
    val coroutineScope = rememberCoroutineScope()
    val experiment = Firebase.remoteConfig.getBoolean("device_scrobbling")

    val enabledApps = runBlocking {
        ctx.scrobblePrefs.data.map { p ->
            p[stringSetPreferencesKey("enabledApps")]
        }.first()
    }

    val enabled = runBlocking {
        ctx.scrobblePrefs.data.map { p ->
            p[booleanPreferencesKey("enabled")]
        }.first()
    }

    val label = if (enabled == true && enabledApps.isNullOrEmpty()) {
        stringResource(R.string.scrobbling_enabled_no_apps)
    } else if (enabled == false) {
        stringResource(R.string.disabled)
    } else {
        pluralStringResource(
            id = R.plurals.scrobble_apps,
            count = enabledApps!!.size,
            enabledApps.size
        )
    }

    user?.let {
        Column(
            modifier = Modifier
                .background(KindaBlack)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(30.dp))
            Text(
                text = stringResource(id = R.string.settings),
                style = Typography.displaySmall
            )
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(LighterGray)
                    .fillMaxWidth()
                    .padding(15.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = defaultImageRequestBuilder(
                                    url = it.user.bestImageUrl,
                                    placeholderType = PlaceholderType.USER
                                ),
                                contentDescription = null,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(43.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(text = user.user.name, style = Typography.titleMedium)
                        }
                        IconButton(onClick = {
                            coroutineScope.launch {
                                ctx.applicationContext.userData.edit {
                                    it.remove(stringPreferencesKey("session_key"))
                                }
                                nav?.navigate("login")
                            }
                        }) {
                            Icon(Icons.Rounded.Logout, null, tint = MostlyRed)
                        }
                    }
                }
            }

            if (experiment) {
                /* Device Scrobbling */
                Spacer(modifier = Modifier.height(20.dp))
                SectionTitle(
                    sectionName = stringResource(R.string.device_scrobbling),
                    badgeName = stringResource(
                        R.string.beta
                    )
                )
                Text(label, style = Typography.bodySmall, modifier = Modifier.alpha(0.55f))
                if (!enabledApps.isNullOrEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy((-7).dp)) {
                        enabledApps.forEach { pckg ->
                            val icon = ctx.packageManager.getApplicationIcon(pckg)
                            Image(
                                icon.toBitmap().asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { nav?.navigate("settings/scrobble") }
                        .fillMaxWidth()
                ) {
                    Text(
                        stringResource(R.string.scrobbling_settings),
                        modifier = Modifier.padding(vertical = 15.dp),
                        style = Typography.bodyLarge
                    )
                    Icon(Icons.Rounded.ChevronRight, null)
                }
                Spacer(Modifier.height(20.dp))
            }

            /* About */
            val patreonIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.patreon.com/musicorumapp"))
            val discordIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/7shqxp9Mg4"))
            val githubIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/musicorum-app/"))
            val lastFmIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://last.fm"))
            SectionTitle(sectionName = stringResource(R.string.about))
            Action(
                leadIcon = R.drawable.patreon_logo,
                text = stringResource(R.string.donate_on_patreon),
                patreonIntent
            )
            Action(
                leadIcon = R.drawable.discord_logo,
                text = stringResource(R.string.join_our_discord_server),
                discordIntent
            )
            Action(
                leadIcon = R.drawable.github_logo,
                text = stringResource(R.string.view_source_code_on_github),
                githubIntent
            )
            Action(
                leadIcon = R.drawable.last_fm_logo,
                text = stringResource(R.string.last_fm_website),
                intent = lastFmIntent
            )

            /* App */
            Text(
                text = stringResource(R.string.made_by),
                style = Typography.bodySmall,
                modifier = Modifier.alpha(.55f)
            )
            Text(
                BuildConfig.VERSION_NAME,
                style = Typography.bodySmall,
                modifier = Modifier.alpha(.55f)
            )
        }
    }
}

@Composable
internal fun SectionTitle(sectionName: String, badgeName: String? = null) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(sectionName, style = Typography.titleLarge)
        badgeName?.let {
            Spacer(Modifier.width(5.dp))
            Box(
                contentAlignment = Alignment.Center, modifier = Modifier
                    .clip(RoundedCornerShape(15.dp))
                    .background(MostlyRed)
                    .padding(vertical = 4.dp, horizontal = 13.dp)
            ) {
                Text(it, fontSize = 13.sp)
            }
        }
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp), onDraw = {
            drawLine(
                Color.White,
                Offset.Zero,
                Offset(size.maxDimension, 0f),
                strokeWidth = 1.5f
            )
        })
    }
}

@Composable
fun Action(leadIcon: Int, text: String, intent: Intent) {
    val ctx = LocalContext.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        modifier = Modifier
            .padding(vertical = 5.dp)
            .clickable { ctx.startActivity(intent) }
            .fillMaxWidth()
            .height(35.dp)
    ) {
        Image(
            painter = painterResource(id = leadIcon),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Text(text, style = Typography.bodyLarge)
    }
}