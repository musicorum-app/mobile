package io.musicorum.mobile.screens

import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.navigation.NavHostController
import androidx.palette.graphics.Palette
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder
import io.musicorum.mobile.components.BottomNavBar
import io.musicorum.mobile.components.HorizontalTrackList
import io.musicorum.mobile.components.LabelType
import io.musicorum.mobile.components.TrackCard
import io.musicorum.mobile.ktor.endpoints.FetchPeriod
import io.musicorum.mobile.serialization.RecentTracks
import io.musicorum.mobile.serialization.Track
import io.musicorum.mobile.serialization.UserData
import io.musicorum.mobile.ui.theme.KindaBlack
import io.musicorum.mobile.ui.theme.SkeletonPrimaryColor
import io.musicorum.mobile.ui.theme.SkeletonSecondaryColor
import io.musicorum.mobile.utils.darkenColor
import io.musicorum.mobile.viewmodels.UserViewModel
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(nav: NavHostController, userViewModel: UserViewModel, sharedPref: SharedPreferences) {
    val user = userViewModel.user.observeAsState()
    val recentTracks = userViewModel.recentTracks.observeAsState()
    val palette = userViewModel.userPalette.observeAsState()
    val weekTracks = userViewModel.weekTracks.observeAsState()
    val ctx = LocalContext.current

    LaunchedEffect(key1 = user.value) {
        if (user.value == null) {
            userViewModel.fetchUser(sharedPref)
        } else {
            if (userViewModel.userPalette.value == null) {
                userViewModel.getPalette(user.value!!.user.image[2].url, ctx)
            }
            if (recentTracks.value == null) {
                userViewModel.fetchRecentTracks(
                    user.value!!.user.name,
                    "${Instant.now().minusSeconds(604800).toEpochMilli() / 1000}",
                    null
                )
            }
            if (weekTracks.value == null) {
                userViewModel.fetchTopTracks(user.value!!.user.name, FetchPeriod.WEEK)
            }
        }
    }

    Scaffold(bottomBar = { BottomNavBar(current = "Home", nav = nav) }) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(Modifier.padding(it)) {
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
                Text(
                    text = "Recent scrobbled",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 20.dp)
                )
                HorizontalTrackList(
                    tracks = recentTracks.value?.recentTracks?.tracks,
                    labelType = LabelType.DATE
                )

                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Most listened • last 7 days",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 20.dp)
                )
                HorizontalTrackList(tracks = weekTracks.value?.topTracks?.tracks, labelType = LabelType.ARTIST_NAME)
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
                painter = rememberAsyncImagePainter(user.image[2].url),
                contentDescription = "user img",
                modifier = Modifier
                    .shadow(elevation = 20.dp, shape = CircleShape)
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


