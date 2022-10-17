package io.musicorum.mobile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import io.musicorum.mobile.serialization.Track

@Composable
fun TrackRow(track: Track) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = rememberAsyncImagePainter(track.image[3].url),
            null,
            modifier = Modifier
                .height(60.dp)
                .clip(RoundedCornerShape(6.dp))
                .aspectRatio(1f)
        )
        Spacer(Modifier.width(10.dp))
        Column {
            Text(text = track.name)
            Text(text = track.artist.displayName, modifier = Modifier.alpha(0.55f))
        }
    }
}