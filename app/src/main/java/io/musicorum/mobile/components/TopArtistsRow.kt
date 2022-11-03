package io.musicorum.mobile.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.serialization.TopArtist
import io.musicorum.mobile.ui.theme.BodySmall
import io.musicorum.mobile.ui.theme.Poppins

private val artistStyle = TextStyle(
    fontFamily = Poppins,
    fontSize = 14.sp,
    fontWeight = FontWeight.Medium
)


@Composable
fun TopArtistsRow(artists: List<TopArtist>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        modifier = Modifier.padding(start = 20.dp)
    ) {
        items(artists) { artist ->
            ArtistCard(artist)
        }
    }
}

@Composable
private fun ArtistCard(artist: TopArtist) {
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
            style = artistStyle,
            modifier = Modifier.width(120.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = artist.playCount.toString() + " plays",
            style = BodySmall,
            modifier = Modifier
                .alpha(0.55f)
                .width(100.dp),
        )
    }
}