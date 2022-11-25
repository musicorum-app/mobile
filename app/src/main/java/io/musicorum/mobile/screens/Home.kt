package io.musicorum.mobile.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
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
import io.musicorum.mobile.LocalUser
import io.musicorum.mobile.R
import io.musicorum.mobile.coil.PlaceholderType
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.components.FriendActivity
import io.musicorum.mobile.components.HorizontalTracksRow
import io.musicorum.mobile.components.LabelType
import io.musicorum.mobile.components.skeletons.GenericCardPlaceholder
import io.musicorum.mobile.ktor.endpoints.FetchPeriod
import io.musicorum.mobile.serialization.RecentTracks
import io.musicorum.mobile.serialization.UserData
import io.musicorum.mobile.ui.theme.*
import io.musicorum.mobile.utils.darkenColor
import io.musicorum.mobile.viewmodels.HomeViewModel
import kotlinx.coroutines.launch
import java.time.Instant

@Composable
fun Home(homeViewModel: HomeViewModel = hiltViewModel(), nav: NavHostController) {
    val user = LocalUser.current
    val recentTracks = homeViewModel.recentTracks.observeAsState().value
    val palette = homeViewModel.userPalette.observeAsState().value
    val weekTracks = homeViewModel.weekTracks.observeAsState().value
    val friends = homeViewModel.friends.observeAsState().value
    val friendsActivity = homeViewModel.friendsActivity.observeAsState().value
    val ctx = LocalContext.current
    val errored = homeViewModel.errored.observeAsState().value

    LaunchedEffect(user, recentTracks, errored) {
        launch {
            user?.let {
                if (homeViewModel.userPalette.value == null) {
                    homeViewModel.getPalette(it.user.bestImageUrl, ctx)
                }
                if (recentTracks == null) {
                    homeViewModel.fetchRecentTracks(
                        it.user.name,
                        "${Instant.now().minusSeconds(604800).toEpochMilli() / 1000}",
                        15,
                        false
                    )
                }
                if (weekTracks == null) {
                    homeViewModel.fetchTopTracks(it.user.name, FetchPeriod.WEEK)
                }
                if (friends == null) {
                    homeViewModel.fetchFriends(it.user.name, 3)
                }
            }
        }
    }


    val isRefreshing = homeViewModel.isRefreshing.collectAsState()
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing.value),
        onRefresh = { homeViewModel.refresh() }) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .background(AlmostBlack)
                .padding(top = 30.dp, bottom = 20.dp)
        ) {
            Text(
                text = "Home",
                style = Typography.titleLarge,
                modifier = Modifier.padding(start = 20.dp, top = 10.dp)
            )

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
                            color = KindaBlack,
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
                nav = nav,
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
                nav = nav,
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
                            friendUsername = friends?.get(i)?.name,
                            nav = nav
                        )
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun UserCard(
    user: UserData,
    palette: Palette,
    recentTracks: RecentTracks?,
    nav: NavHostController
) {
    val vibrant = Color(palette.getDarkVibrantColor(0))
    val darken = darkenColor(vibrant.toArgb(), 0.55f)
    val gradient = listOf(vibrant, darken)
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


