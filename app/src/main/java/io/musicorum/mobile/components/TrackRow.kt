package io.musicorum.mobile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.serialization.NavigationTrack
import io.musicorum.mobile.serialization.Track
import io.musicorum.mobile.ui.theme.AlmostBlack
import io.musicorum.mobile.ui.theme.BodyLarge
import io.musicorum.mobile.ui.theme.Subtitle1
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackRow(track: Track, nav: NavHostController, favoriteIcon: Boolean = true) {
    val partialTrack = NavigationTrack(track.name, track.artist.name)
    val dest = Json.encodeToString(partialTrack)
    val listColors = ListItemDefaults.colors(
        containerColor = AlmostBlack
    )

    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { nav.navigate("track/$dest") { } }) {
        ListItem(
            headlineText = { Text(text = track.name, style = BodyLarge) },
            colors = listColors,
            supportingText = {
                Text(
                    text = track.artist.name,
                    modifier = Modifier.alpha(0.55f),
                    style = Subtitle1
                )
            },
            leadingContent = {
                AsyncImage(
                    model = defaultImageRequestBuilder(url = track.bestImageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .height(60.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .aspectRatio(1f)
                )
            },
            trailingContent = {
                if (favoriteIcon) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Outlined.Favorite, contentDescription = null)
                    }
                }
            }
        )
    }
}