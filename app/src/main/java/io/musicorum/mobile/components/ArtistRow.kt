package io.musicorum.mobile.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.musicorum.mobile.LocalNavigation
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.serialization.entities.Artist
import io.musicorum.mobile.ui.theme.Typography

@Composable
fun ArtistRow(artists: List<Artist>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        item {
            Spacer(modifier = Modifier.width(5.dp))
        }
        items(artists) { artist ->
            ArtistCard(artist)
        }
        item {
            Spacer(modifier = Modifier.width(5.dp))
        }
    }
}

@Composable
private fun ArtistCard(artist: Artist) {
    val nav = LocalNavigation.current
    val interactionSource = remember { MutableInteractionSource() }
    Column(modifier = Modifier.clickable(interactionSource = interactionSource, indication = null) {
        nav?.navigate("artist/${artist.name}")
    }) {
        AsyncImage(
            model = defaultImageRequestBuilder(url = artist.bestImageUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .indication(interactionSource, LocalIndication.current)
        )
        Text(
            text = artist.name,
            style = Typography.bodyLarge,
            modifier = Modifier.width(120.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
