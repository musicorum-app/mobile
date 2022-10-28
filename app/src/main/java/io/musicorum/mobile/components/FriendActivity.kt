package io.musicorum.mobile.components

import android.text.format.DateUtils
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import io.musicorum.mobile.coil.PlaceholderType
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.serialization.Track
import io.musicorum.mobile.ui.theme.BodySmall
import io.musicorum.mobile.ui.theme.Poppins

@Composable
fun FriendActivity(track: Track, friendImageUrl: String) {
    Column {
        Box(modifier = Modifier.size(135.dp)) {
            AsyncImage(
                model = defaultImageRequestBuilder(url = track.bestImageUrl),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .align(Alignment.Center)
            )
            AsyncImage(
                model = defaultImageRequestBuilder(url = friendImageUrl, PlaceholderType.USER),
                null,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .border(3.dp, Color.Black, CircleShape)
                    .align(Alignment.BottomEnd)
            )
        }
        Text(
            text = track.name,
            fontFamily = Poppins,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 5.dp)
        )
        val date = if (track.attributes?.nowPlaying.toBoolean()) {
            "Now Playing"
        } else {
            val now = System.currentTimeMillis()
            DateUtils.getRelativeTimeSpanString(
                track.date!!.uts.toLong() * 1000,
                now,
                DateUtils.SECOND_IN_MILLIS
            ).toString()
        }
        Text(text = date, style = BodySmall, modifier = Modifier
            .alpha(0.55f)
            .padding(start = 5.dp))
    }
}