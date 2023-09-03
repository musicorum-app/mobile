package io.musicorum.mobile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import io.musicorum.mobile.serialization.entities.Album
import io.musicorum.mobile.ui.theme.ContentSecondary

@Composable
fun AlbumListItem(album: Album) {
    val model = defaultImageRequestBuilder(
        url = album.bestImageUrl,
        placeholderType = PlaceholderType.ALBUM
    )
    val nav = LocalNavigation.current

    ListItem(
        headlineContent = { Text(album.name) },
        supportingContent = { Text(album.artist ?: "Unknown Artist", color = ContentSecondary) },
        leadingContent = {
            AsyncImage(
                model = model, contentDescription = null, modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .size(46.dp)
            )
        },
        modifier = Modifier.clickable {
            nav?.navigate(Routes.album(album))
        }
    )
}