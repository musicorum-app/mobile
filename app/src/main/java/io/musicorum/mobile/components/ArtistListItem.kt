package io.musicorum.mobile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.musicorum.mobile.LocalNavigation
import io.musicorum.mobile.coil.PlaceholderType
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.router.Routes
import io.musicorum.mobile.serialization.entities.Artist

@Composable
fun ArtistListItem(artist: Artist) {
    val model = defaultImageRequestBuilder(
        url = artist.bestImageUrl,
        placeholderType = PlaceholderType.ARTIST
    )
    val nav = LocalNavigation.current
    ListItem(
        headlineContent = { Text(artist.name) },
        leadingContent = {
            AsyncImage(
                model = model, contentDescription = null, modifier = Modifier
                    .clip(CircleShape)
                    .size(46.dp)
            )
        },
        modifier = Modifier.clickable {
            nav?.navigate(Routes.artist(artist.name))
        }
    )
}