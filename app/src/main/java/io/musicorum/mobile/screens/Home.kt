package io.musicorum.mobile.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.NavHostController
import androidx.palette.graphics.Palette
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder
import io.musicorum.mobile.components.BottomNavBar
import io.musicorum.mobile.components.FriendActivity
import io.musicorum.mobile.components.HorizontalTrackList
import io.musicorum.mobile.components.LabelType
import io.musicorum.mobile.dataStore
import io.musicorum.mobile.ktor.endpoints.FetchPeriod
import io.musicorum.mobile.serialization.RecentTracks
import io.musicorum.mobile.serialization.UserData
import io.musicorum.mobile.ui.theme.BodyMedium
import io.musicorum.mobile.ui.theme.KindaBlack
import io.musicorum.mobile.ui.theme.SkeletonSecondaryColor
import io.musicorum.mobile.utils.darkenColor
import io.musicorum.mobile.viewmodels.HomeViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(homeViewModel: HomeViewModel, nav: NavHostController) {
    val user = homeViewModel.user.observeAsState()
    val recentTracks = homeViewModel.recentTracks.observeAsState()
    val palette = homeViewModel.userPalette.observeAsState()
    val weekTracks = homeViewModel.weekTracks.observeAsState()
    val friends = homeViewModel.friends.observeAsState()
    val friendsActivity = homeViewModel.friendsActivity.observeAsState()
    val ctx = LocalContext.current

    val screenState = rememberScrollState()


    LaunchedEffect(key1 = user.value) {
        if (user.value == null) {
            val sessionKey = ctx.applicationContext.dataStore.data.map { prefs ->
                prefs[stringPreferencesKey("session_key")]!!
            }
            homeViewModel.fetchUser(sessionKey.first())
        } else {
            if (homeViewModel.userPalette.value == null) {
                homeViewModel.getPalette(user.value!!.user.bestImageUrl, ctx)
            }
            if (recentTracks.value == null) {
                homeViewModel.fetchRecentTracks(
                    user.value!!.user.name,
                    "${Instant.now().minusSeconds(604800).toEpochMilli() / 1000}",
                    15,
                    false
                )
            }
            if (weekTracks.value == null) {
                homeViewModel.fetchTopTracks(user.value!!.user.name, FetchPeriod.WEEK)
            }
            if (friends.value == null) {
                homeViewModel.fetchFriends(user.value!!.user.name, 3)
            }
        }
    }

    Scaffold(bottomBar = { BottomNavBar(nav) }) {
        Column(
            Modifier
                .padding(it)
                .verticalScroll(screenState)
        ) {
            Text(
                text = "Home",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 20.dp, top = 10.dp)
            )
            if (user.value != null && palette.value != null) {
                UserCard(user.value!!.user, palette.value!!, recentTracks.value)

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
                    text = "Recent scrobbles",
                    style = BodyMedium,
                    modifier = Modifier.padding(start = 20.dp)
                )
                IconButton(onClick = { nav.navigate("recentScrobbles") }) {
                    Icon(Icons.Rounded.ChevronRight, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalTrackList(
                tracks = recentTracks.value?.recentTracks?.tracks,
                labelType = LabelType.DATE,
                nav = nav
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
                    text = "Most listened • last 7 days",
                    style = BodyMedium,
                    modifier = Modifier.padding(start = 20.dp)
                )
                IconButton(onClick = { nav.navigate("mostListened") }) {
                    Icon(Icons.Rounded.ChevronRight, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalTrackList(
                tracks = weekTracks.value?.topTracks?.tracks,
                labelType = LabelType.ARTIST_NAME,
                nav = nav
            )

            if (friends.value != null && friendsActivity.value != null) {
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Friends Activity",
                    style = BodyMedium,
                    modifier = Modifier.padding(start = 20.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    friendsActivity.value!!.forEachIndexed { i, rt ->
                        FriendActivity(
                            track = rt.recentTracks.tracks[0],
                            friendImageUrl = friends.value!![i].bestImageUrl
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UserCard(user: UserData, palette: Palette, recentTracks: RecentTracks?) {
    val vibrant = Color(palette.getDominantColor(0))
    val darken = darkenColor(palette.getDominantColor(0), 0.18f)
    val gradient = listOf(vibrant, darken)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(20.dp, 20.dp, 20.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(brush = Brush.linearGradient(gradient))

    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(user.bestImageUrl),
                contentDescription = "user img",
                modifier = Modifier
                    .shadow(elevation = 20.dp, shape = CircleShape)
                    .aspectRatio(1f)
                    .clip(CircleShape),
                alignment = Alignment.CenterStart
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = user.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = recentTracks?.recentTracks?.recentTracksAttributes?.total + " scrobbles • last 7 days",
                    style = MaterialTheme.typography.labelMedium,
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


