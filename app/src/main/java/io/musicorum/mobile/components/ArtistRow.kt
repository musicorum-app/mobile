package io.musicorum.mobile.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.serialization.Artist
import io.musicorum.mobile.ui.theme.Typography

@Composable
fun ArtistRow(artists: List<Artist>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        modifier = Modifier
            .padding(start = 20.dp)
            .fillMaxWidth()
    ) {
        items(artists) { artist ->
            ArtistCard(artist)
        }
    }
}

@Composable
private fun ArtistCard(artist: Artist) {
    Column {
        AsyncImage(
            model = defaultImageRequestBuilder(url = artist.bestImageUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
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
