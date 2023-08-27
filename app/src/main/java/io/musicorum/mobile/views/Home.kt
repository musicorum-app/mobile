package io.musicorum.mobile.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.musicorum.mobile.LocalNavigation
import io.musicorum.mobile.R
import io.musicorum.mobile.coil.PlaceholderType
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.components.FriendActivity
import io.musicorum.mobile.components.HorizontalTracksRow
import io.musicorum.mobile.components.LabelType
import io.musicorum.mobile.components.skeletons.GenericCardPlaceholder
import io.musicorum.mobile.serialization.RecentTracks
import io.musicorum.mobile.serialization.UserData
import io.musicorum.mobile.ui.theme.KindaBlack
import io.musicorum.mobile.ui.theme.LighterGray
import io.musicorum.mobile.ui.theme.SkeletonSecondaryColor
import io.musicorum.mobile.ui.theme.Subtitle1
import io.musicorum.mobile.ui.theme.Typography
import io.musicorum.mobile.utils.getDarkenGradient
import io.musicorum.mobile.viewmodels.HomeViewModel

@Composable
fun Home(vm: HomeViewModel = hiltViewModel()) {
    val user = vm.user.observeAsState().value
    val recentTracks = vm.recentTracks.observeAsState().value
    val palette = vm.userPalette.observeAsState().value
    val weekTracks = vm.weekTracks.observeAsState().value
    val friends = vm.friends.observeAsState().value
    val friendsActivity = vm.friendsActivity.observeAsState().value
    val errored = vm.errored.observeAsState().value
    val nav = LocalNavigation.current!!
    val isRefreshing = vm.isRefreshing.collectAsState()

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing.value),
        onRefresh = { vm.refresh() }) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .background(KindaBlack)
                .padding(top = 30.dp, bottom = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Home",
                    style = Typography.displaySmall,
                    modifier = Modifier.padding(start = 20.dp)
                )

                IconButton(onClick = { nav.navigate("settings") }) {
                    Icon(Icons.Rounded.Settings, contentDescription = null)
                }
            }

            if (user != null && palette != null) {
                UserCard(user.user, palette, recentTracks, nav)
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(20.dp, 20.dp, 20.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .placeholder(
                            true,
                            color = LighterGray,
                            highlight = PlaceholderHighlight.shimmer()
                        )
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable { nav.navigate("recentScrobbles") },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.recent_scrobbles),
                    style = Typography.headlineSmall,
                    modifier = Modifier.padding(start = 20.dp)
                )
                IconButton(onClick = { nav.navigate("recentScrobbles") }) {
                    Icon(Icons.Rounded.ChevronRight, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalTracksRow(
                tracks = recentTracks?.recentTracks?.tracks,
                labelType = LabelType.DATE,
                errored = recentTracks?.recentTracks?.tracks?.isEmpty()
            )

            Spacer(Modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable { nav.navigate("mostListened") },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.most_listened_week),
                    style = Typography.headlineSmall,
                    modifier = Modifier.padding(start = 20.dp)
                )
                IconButton(onClick = { nav.navigate("mostListened") }) {
                    Icon(Icons.Rounded.ChevronRight, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalTracksRow(
                tracks = weekTracks?.topTracks?.tracks,
                labelType = LabelType.ARTIST_NAME,
                errored = weekTracks?.topTracks?.tracks?.isEmpty()
            )

            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.friends_activity),
                style = Typography.headlineSmall,
                modifier = Modifier.padding(start = 20.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .padding(start = 20.dp)
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (friendsActivity == null && friends == null) {
                    if (errored == true) {
                        Text(
                            text = "When you follow people, their listening activity will appear here",
                            softWrap = true,
                            style = Subtitle1
                        )
                    } else {
                        GenericCardPlaceholder(visible = true)
                        GenericCardPlaceholder(visible = true)
                        GenericCardPlaceholder(visible = true)
                    }
                } else {
                    friendsActivity?.forEachIndexed { i, rt ->
                        FriendActivity(
                            track = rt.recentTracks.tracks[0],
                            friendImageUrl = friends?.get(i)?.bestImageUrl,
                            friendUsername = friends?.get(i)?.name
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UserCard(
    user: UserData,
    palette: Palette,
    recentTracks: RecentTracks?,
    nav: NavHostController
) {
    var vibrant = Color(palette.getVibrantColor(0))
    if (palette.vibrantSwatch == null) {
        vibrant = Color(palette.getDominantColor(0))
    }
    val gradient = getDarkenGradient(vibrant)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(20.dp, 20.dp, 20.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(brush = Brush.linearGradient(gradient))
            .clickable { nav.navigate("profile") }

    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = defaultImageRequestBuilder(
                    url = user.bestImageUrl,
                    PlaceholderType.USER
                ), contentDescription = "user profile pic",
                modifier = Modifier
                    .shadow(elevation = 20.dp, shape = CircleShape)
                    .aspectRatio(1f)
                    .clip(CircleShape),
                alignment = Alignment.CenterStart
            )

            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = user.name, style = Typography.headlineMedium)
                val total = recentTracks?.recentTracks?.recentTracksAttributes?.total?.toInt() ?: 0
                Text(
                    text = pluralStringResource(
                        id = R.plurals.total_scrobbles_week,
                        count = total,
                        total
                    ),
                    style = Typography.bodyMedium,
                    modifier = Modifier
                        .alpha(0.55f)
                        .placeholder(
                            recentTracks == null,
                            highlight = PlaceholderHighlight.shimmer(),
                            color = SkeletonSecondaryColor
                        )
                )
            }
        }
    }
}


