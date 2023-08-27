package io.musicorum.mobile.views.settings

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
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import io.musicorum.mobile.BuildConfig
import io.musicorum.mobile.LocalNavigation
import io.musicorum.mobile.LocalUser
import io.musicorum.mobile.R
import io.musicorum.mobile.coil.PlaceholderType
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.router.Routes
import io.musicorum.mobile.ui.theme.LighterGray
import io.musicorum.mobile.ui.theme.MostlyRed
import io.musicorum.mobile.ui.theme.Typography
import io.musicorum.mobile.viewmodels.SettingsVm

@Composable
fun Settings(viewModel: SettingsVm = viewModel()) {
    val user = LocalUser.current
    val ctx = LocalContext.current
    val nav = LocalNavigation.current
    val enabledApps = viewModel.enabledApps.observeAsState().value
    val enabled = viewModel.deviceScrobble.observeAsState().value

    val patreonUrl = "https://www.patreon.com/musicorumapp"
    val discordInvite = "https://discord.gg/7shqxp9Mg4"
    val githubUrl = "https://github.com/musicorum-app/"
    val lastFmUrl = "https://last.fm"

    val label = if (enabled == true && enabledApps.isNullOrEmpty()) {
        stringResource(R.string.scrobbling_enabled_no_apps)
    } else if (enabled == false || enabled == null) {
        stringResource(R.string.disabled)
    } else {
        pluralStringResource(
            id = R.plurals.scrobble_apps,
            count = enabledApps!!.size,
            enabledApps.size
        )
    }

    LaunchedEffect(Unit) {
        viewModel.getScrobbleInfo()
    }

    Scaffold(topBar = { TopAppBar() }) {
        Column(modifier = Modifier.padding(it)) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(LighterGray)
                    .fillMaxWidth()
                    .padding(15.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = defaultImageRequestBuilder(
                                    url = user?.user?.bestImageUrl,
                                    placeholderType = PlaceholderType.USER
                                ),
                                contentDescription = null,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(43.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(text = user?.user?.name ?: "", style = Typography.titleMedium)
                        }
                        IconButton(onClick = { viewModel.logout {} }) {
                            Icon(Icons.Rounded.Logout, null, tint = MostlyRed)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            SectionTitle(
                sectionName = stringResource(R.string.device_scrobbling),
                badgeName = stringResource(R.string.beta)
            )
            Text(
                label,
                style = Typography.bodySmall,
                modifier = Modifier
                    .alpha(0.55f)
                    .padding(start = 16.dp)
            )

            if (!enabledApps.isNullOrEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy((-7).dp),
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    enabledApps.forEach { pkg ->
                        val icon = ctx.packageManager.getApplicationIcon(pkg)
                        Image(
                            icon.toBitmap().asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }
            }

            ListItem(
                headlineContent = { Text(stringResource(id = R.string.scrobbling_settings)) },
                trailingContent = { Icon(Icons.Rounded.ChevronRight, null) },
                modifier = Modifier.clickable {
                    nav?.navigate(Routes.scrobbleSettings)
                }
            )

            Spacer(Modifier.height(20.dp))
            SectionTitle(sectionName = stringResource(R.string.about))
            ListItem(
                headlineContent = { Text(text = stringResource(R.string.donate_on_patreon)) },
                leadingContent = {
                    Image(painterResource(id = R.drawable.patreon_logo), null)
                },
                modifier = Modifier.clickable { viewModel.launchUrl(patreonUrl) }
            )

            ListItem(
                headlineContent = { Text(text = stringResource(R.string.join_our_discord_server)) },
                leadingContent = {
                    Image(painterResource(id = R.drawable.discord_logo), null)
                },
                modifier = Modifier.clickable { viewModel.launchUrl(discordInvite) }
            )

            ListItem(
                headlineContent = { Text(text = stringResource(R.string.view_source_code_on_github)) },
                leadingContent = {
                    Image(painterResource(id = R.drawable.github_logo), null)
                },
                modifier = Modifier.clickable { viewModel.launchUrl(githubUrl) }
            )

            ListItem(
                headlineContent = { Text(text = stringResource(R.string.last_fm_website)) },
                leadingContent = {
                    Image(painterResource(id = R.drawable.last_fm_logo), null)
                },
                modifier = Modifier.clickable { viewModel.launchUrl(lastFmUrl) }
            )

            //* App *//*
            Text(
                text = stringResource(R.string.made_by),
                style = Typography.bodySmall,
                modifier = Modifier
                    .alpha(.55f)
                    .padding(start = 16.dp)
            )
            Text(
                BuildConfig.VERSION_NAME,
                style = Typography.bodySmall,
                modifier = Modifier
                    .alpha(.55f)
                    .padding(start = 16.dp)
            )
        }
    }
}


@Composable
internal fun SectionTitle(sectionName: String, badgeName: String? = null, padding: Boolean = true) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 16.dp)) {
        val padValue = if (padding) 16.dp else 0.dp
        Text(
            sectionName,
            style = Typography.titleMedium,
            modifier = Modifier.padding(start = padValue)
        )
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
            .padding(start = 10.dp),
            onDraw = {
                drawLine(
                    Color.White,
                    Offset.Zero,
                    Offset(size.maxDimension, 0f),
                    strokeWidth = 1.5f
                )
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar() {
    val nav = LocalNavigation.current
    MediumTopAppBar(
        title = { Text(stringResource(id = R.string.settings)) },
        navigationIcon = {
            IconButton(onClick = { nav?.popBackStack() }) {
                Icon(Icons.Rounded.ArrowBack, null)
            }
        }
    )
}