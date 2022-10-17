package io.musicorum.mobile.components

import android.text.format.DateUtils
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import io.musicorum.mobile.serialization.Track
import io.musicorum.mobile.ui.theme.Poppins

val label = TextStyle(
    fontFamily = Poppins,
    fontWeight = FontWeight(500),
    fontSize = 12.sp
)

enum class LabelType {
    DATE, ARTIST_NAME
}

@Composable
fun TrackCard(track: Track, labelType: LabelType) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .width(150.dp)
            .height(200.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(track.image[3].url),
            null,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .aspectRatio(1f)
        )
        Text(
            text = track.name,
            textAlign = TextAlign.Start,
            style = label,
            modifier = Modifier.padding(top = 7.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (labelType == LabelType.DATE) {
            val text = if (track.attributes?.nowPlaying.toBoolean()) {
                "Scrobbling Now"
            } else {
                val now = System.currentTimeMillis()
                DateUtils.getRelativeTimeSpanString(
                    track.date!!.uts.toLong() * 1000,
                    now,
                    DateUtils.SECOND_IN_MILLIS
                ).toString()
            }
            Text(text, modifier = Modifier.alpha(0.55f), style = label)
        } else if (labelType == LabelType.ARTIST_NAME) {
            Text(track.artist.displayName, modifier = Modifier.alpha(0.55f), style = label)
        }
    }
}
