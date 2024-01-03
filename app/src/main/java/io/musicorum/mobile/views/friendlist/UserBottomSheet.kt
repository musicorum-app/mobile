package io.musicorum.mobile.views.friendlist

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.musicorum.mobile.LocalNavigation
import io.musicorum.mobile.coil.PlaceholderType
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.router.Routes
import io.musicorum.mobile.serialization.UserData
import io.musicorum.mobile.ui.theme.LighterGray
import io.musicorum.mobile.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun UserBottomSheet(
    userData: UserData,
    isPinned: Boolean,
    onDismiss: () -> Unit,
    onPin: () -> Unit,
) {
    val nav = LocalNavigation.current
    val userUri = Uri.parse("https://last.fm/user/${userData.name}")
    val userIntent = Intent(Intent.ACTION_VIEW, userUri)
    val ctx = LocalContext.current

    val userModel = defaultImageRequestBuilder(
        url = userData.bestImageUrl,
        placeholderType = PlaceholderType.USER
    )
    val listItemColors = ListItemDefaults.colors(
        containerColor = LighterGray,
    )
    ModalBottomSheet(
        modifier = Modifier.consumeWindowInsets(WindowInsets.navigationBars),
        onDismissRequest = { onDismiss() },
        containerColor = LighterGray,
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            AsyncImage(
                model = userModel, contentDescription = null, modifier = Modifier
                    .clip(CircleShape)
                    .size(80.dp)
            )
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(userData.name, style = Typography.headlineMedium)
                userData.realName?.let {
                    Text(it, style = Typography.titleMedium)
                }
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        if (isPinned) {
            ListItem(
                colors = listItemColors,
                headlineContent = { Text("Unpin") },
                leadingContent = { Icon(Icons.Rounded.Close, contentDescription = null) },
                modifier = Modifier.clickable { onPin() }
            )
        } else {
            ListItem(
                colors = listItemColors,
                headlineContent = { Text("Pin on home screen") },
                leadingContent = { Icon(Icons.Rounded.PushPin, contentDescription = null) },
                modifier = Modifier.clickable { onPin() }
            )
        }

        ListItem(
            colors = listItemColors,
            headlineContent = { Text("View ${userData.name}'s profile") },
            leadingContent = {
                AsyncImage(
                    model = userModel,
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(25.dp)
                )
            },
            modifier = Modifier.clickable {
                nav?.navigate(Routes.user(userData.name))
            }
        )

        ListItem(
            colors = listItemColors,
            headlineContent = { Text("Open on Last.fm") },
            leadingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                    contentDescription = null
                )
            },
            modifier = Modifier.clickable { ctx.startActivity(userIntent) }
        )
    }
}