package io.musicorum.mobile.screens

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.NavHostController
import androidx.palette.graphics.Palette
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder
import io.musicorum.mobile.R
import io.musicorum.mobile.components.FriendActivity
import io.musicorum.mobile.components.HorizontalTracksRow
import io.musicorum.mobile.components.LabelType
import io.musicorum.mobile.dataStore
import io.musicorum.mobile.ktor.endpoints.FetchPeriod
import io.musicorum.mobile.serialization.RecentTracks
import io.musicorum.mobile.serialization.UserData
import io.musicorum.mobile.ui.theme.AlmostBlack
import io.musicorum.mobile.ui.theme.BodyMedium
import io.musicorum.mobile.ui.theme.KindaBlack
import io.musicorum.mobile.ui.theme.SkeletonSecondaryColor
import io.musicorum.mobile.utils.darkenColor
import io.musicorum.mobile.viewmodels.HomeViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant

@Composable
fun Home(homeViewModel: HomeViewModel, nav: NavHostController) {
    val user = homeViewModel.user.observeAsState().value
    val recentTracks = homeViewModel.recentTracks.observeAsState().value
    val palette = homeViewModel.userPalette.observeAsState().value
    val weekTracks = homeViewModel.weekTracks.observeAsState().value
    val friends = homeViewModel.friends.observeAsState().value
    val friendsActivity = homeViewModel.friendsActivity.observeAsState().value
    val ctx = LocalContext.current

    val screenState = rememberScrollState()
    LaunchedEffect(key1 = user) {
        if (user == null) {
            val sessionKey = ctx.applicationContext.dataStore.data.map { prefs ->
                prefs[stringPreferencesKey("session_key")]
            }
            if (sessionKey.first() == null) {
                nav.navigate("login")
            } else {
                homeViewModel.fetchUser(sessionKey.first()!!)
            }

        } else {
            launch {
                if (homeViewModel.userPalette.value == null) {
                    homeViewModel.getPalette(user.user.bestImageUrl, ctx)
                }
                if (recentTracks == null) {
                    homeViewModel.fetchRecentTracks(
                        user.user.name,
                        "${Instant.now().minusSeconds(604800).toEpochMilli() / 1000}",
                        15,
                        false
                    )
                }
                if (weekTracks == null) {
                    homeViewModel.fetchTopTracks(user.user.name, FetchPeriod.WEEK)
                }
                if (friends == null) {
                    homeViewModel.fetchFriends(user.user.name, 3)
                }
            }
        }
    }

    Column(
        Modifier
            .verticalScroll(screenState)
            .background(AlmostBlack)
    ) {
        Text(
            text = "Home",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 20.dp, top = 10.dp)
        )
        if (user != null && palette != null) {
            Log.d("home", "rendering user card...")
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
                style = BodyMedium,
                modifier = Modifier.padding(start = 20.dp)
            )
            IconButton(onClick = { nav.navigate("recentScrobbles") }) {
                Icon(Icons.Rounded.ChevronRight, contentDescription = null)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Log.d("home", "rendering horizontal tracklist (recent)...")
        HorizontalTracksRow(
            tracks = recentTracks?.recentTracks?.tracks,
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
                text = stringResource(R.string.most_listened_week),
                style = BodyMedium,
                modifier = Modifier.padding(start = 20.dp)
            )
            IconButton(onClick = { nav.navigate("mostListened") }) {
                Icon(Icons.Rounded.ChevronRight, contentDescription = null)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Log.d("home", "rendering recent tracklist (toptracks)...")
        HorizontalTracksRow(
            tracks = weekTracks?.topTracks?.tracks,
            labelType = LabelType.ARTIST_NAME,
            nav = nav
        )

        if (friends != null && friendsActivity != null) {
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.friends_activity),
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
                friendsActivity.forEachIndexed { i, rt ->
                    Log.d("home", "rendering friend activity...")
                    FriendActivity(
                        track = rt.recentTracks.tracks[0],
                        friendImageUrl = friends[i].bestImageUrl,
                        friendUsername = friends[i].name,
                        nav = nav
                    )
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
            .clickable { nav.navigate("user/${user.name}") }

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
                val total = recentTracks?.recentTracks?.recentTracksAttributes?.total?.toInt() ?: 0
                Text(
                    text = pluralStringResource(
                        id = R.plurals.total_scrobbles_week,
                        count = total,
                        total
                    ),
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


