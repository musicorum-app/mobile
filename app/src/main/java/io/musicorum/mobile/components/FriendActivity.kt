package io.musicorum.mobile.components

import android.text.format.DateUtils
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import io.musicorum.mobile.LocalAnalytics
import io.musicorum.mobile.LocalNavigation
import io.musicorum.mobile.R
import io.musicorum.mobile.coil.PlaceholderType
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.router.Routes
import io.musicorum.mobile.serialization.NavigationTrack
import io.musicorum.mobile.serialization.entities.Track
import io.musicorum.mobile.ui.theme.ContentSecondary
import io.musicorum.mobile.ui.theme.KindaBlack
import io.musicorum.mobile.ui.theme.LighterGray
import io.musicorum.mobile.ui.theme.Typography
import io.musicorum.mobile.utils.Rive
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FriendActivity(
    track: Track,
    friendImageUrl: String?,
    friendUsername: String?,
    isPinned: Boolean,
    onUnpin: (() -> Unit)? = null,
) {
    val analytics = LocalAnalytics.current!!
    val nav = LocalNavigation.current
    val showSheet = remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    if (showSheet.value) {
        val colors = ListItemDefaults.colors(
            containerColor = LighterGray
        )
        TrackSheet(track = track, show = showSheet) {
            ListItem(
                headlineContent = { Text("View ${friendUsername}'s profile") },
                leadingContent = {
                    AsyncImage(
                        model = defaultImageRequestBuilder(
                            url = friendImageUrl,
                            PlaceholderType.USER
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                    )
                },
                colors = colors,
                modifier = Modifier.clickable {
                    friendUsername?.let {
                        showSheet.value = false
                        nav?.navigate(Routes.user(it))
                    }
                }
            )
            if (isPinned) {
                ListItem(
                    colors = colors,
                    headlineContent = { Text("Unpin $friendUsername") },
                    leadingContent = {
                        Icon(Icons.Rounded.Close, null)
                    },
                    modifier = Modifier.clickable {
                        showSheet.value = false
                        onUnpin?.invoke()
                    }
                )
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.size(120.dp)) {
            val navTrack = NavigationTrack(track.name, track.artist.name)
            val encodedTrack = Json.encodeToString(navTrack)
            AsyncImage(
                model = defaultImageRequestBuilder(url = track.bestImageUrl),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .align(Alignment.CenterStart)
                    .combinedClickable(
                        onClick = {
                            nav?.navigate("track/$encodedTrack")
                            analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                                param(FirebaseAnalytics.Param.ITEM_NAME, "friend_activity_track")
                            }
                        },
                        onLongClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            showSheet.value = true
                        }
                    )
            )
            AsyncImage(
                model = defaultImageRequestBuilder(url = friendImageUrl, PlaceholderType.USER),
                null,
                modifier = Modifier
                    .size(40.dp)
                    .offset(5.dp, 10.dp)
                    .clip(CircleShape)
                    .background(color = KindaBlack, shape = CircleShape)
                    .padding(3.dp)
                    .clip(CircleShape)
                    .align(Alignment.BottomEnd)
                    .clickable {
                        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
                            param(FirebaseAnalytics.Param.ITEM_NAME, "friend_activity_profile")

                        }
                        nav?.navigate("user/$friendUsername")
                    }
            )
            if (isPinned) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(5.dp, (-10).dp)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(color = LighterGray, shape = CircleShape)
                        .border(3.dp, KindaBlack, CircleShape)
                        .padding(7.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PushPin,
                        contentDescription = null,
                        modifier = Modifier
                            .rotate((30f))
                            .align(Alignment.Center)
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row {
            if (track.attributes?.nowPlaying == "true") {
                Rive.AnimationFor(
                    _alpha = 0.55f,
                    id = R.raw.nowplaying,
                    modifier = Modifier
                        .size(15.dp)
                        .padding(end = 3.dp)
                )
            }
            Text(
                text = track.name,
                style = Typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
            text = date,
            style = Typography.bodyMedium,
            color = ContentSecondary
        )
    }
}