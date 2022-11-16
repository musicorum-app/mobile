package io.musicorum.mobile.components

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import io.musicorum.mobile.R
import io.musicorum.mobile.coil.PlaceholderType
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.serialization.Track
import io.musicorum.mobile.ui.theme.AlmostBlack
import io.musicorum.mobile.ui.theme.BodySmall
import io.musicorum.mobile.ui.theme.Poppins

import io.musicorum.mobile.utils.Rive

@Composable
fun FriendActivity(
    track: Track,
    friendImageUrl: String?,
    friendUsername: String?,
    nav: NavHostController
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.size(120.dp)) {
            AsyncImage(
                model = defaultImageRequestBuilder(url = track.bestImageUrl),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .align(Alignment.CenterStart)
            )
            AsyncImage(
                model = defaultImageRequestBuilder(url = friendImageUrl, PlaceholderType.USER),
                null,
                modifier = Modifier
                    .size(40.dp)
                    .offset(5.dp, 10.dp)
                    .clip(CircleShape)
                    .background(color = AlmostBlack, shape = CircleShape)
                    .padding(3.dp)
                    .clip(CircleShape)
                    .align(Alignment.BottomEnd)
                    .clickable { nav.navigate("user/$friendUsername") }
            )
        }
        Spacer(Modifier.height(10.dp))
        Row {
            if (track.attributes?.nowPlaying == "true") {
                Rive.AnimationFor(
                    id = R.layout.nowplaying_view,
                    modifier = Modifier
                    .size(15.dp)
                    .padding(end = 3.dp))
            }
            Text(
                text = track.name,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 14.sp,
                modifier = Modifier.width(105.dp)
            )
        }
        val date = if (track.attributes?.nowPlaying.toBoolean()) {
            stringResource(R.string.scrobbling_now)
        } else {
            val now = System.currentTimeMillis()
            DateUtils.getRelativeTimeSpanString(
                track.date!!.uts.toLong() * 1000,
                now,
                DateUtils.SECOND_IN_MILLIS
            ).toString()
        }
        Text(
            text = date, style = BodySmall, modifier = Modifier
                .alpha(0.55f)
        )
    }
}