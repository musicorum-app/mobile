package io.musicorum.mobile.screens.individual

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.musicorum.mobile.R
import io.musicorum.mobile.components.*
import io.musicorum.mobile.ktor.endpoints.FetchPeriod
import io.musicorum.mobile.ui.theme.AlmostBlack
import io.musicorum.mobile.ui.theme.Heading2
import io.musicorum.mobile.ui.theme.Heading4
import io.musicorum.mobile.ui.theme.Subtitle1
import io.musicorum.mobile.viewmodels.UserViewModel

@Composable
fun User(
    username: String,
    userViewModel: UserViewModel = viewModel(),
    nav: NavHostController
) {
    val user = userViewModel.user.observeAsState().value
    val topArtists = userViewModel.topArtists.observeAsState().value
    val recentScrobbles = userViewModel.recentTracks.observeAsState().value?.recentTracks?.tracks
    val topAlbums = userViewModel.topAlbums.observeAsState().value

    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = user) {
        if (user == null) {
            userViewModel.getUser(username)
        } else {
            if (topArtists == null) {
                userViewModel.getTopArtists(username, null, FetchPeriod.MONTH)
            }
            if (recentScrobbles == null) {
                userViewModel.getRecentTracks(user.user.name, limit = 4, null)
            }
            if (topAlbums == null) {
                userViewModel.getTopAlbums(user.user.name, FetchPeriod.MONTH, null)
            }
        }
    }

    if (user == null) {
        Row(
            Modifier
                .fillMaxSize()
                .background(AlmostBlack),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(scrollState)
                .background(AlmostBlack)
        ) {
            GradientHeader(
                backgroundUrl = topArtists?.topArtists?.artists?.getOrNull(0)?.bestImageUrl,
                coverUrl = user.user.bestImageUrl,
                shape = CircleShape
            )
            Text(text = user.user.name, style = Heading2)
            Row {
                user.user.realName?.let {
                    Text(text = "$it • ", modifier = Modifier.alpha(0.55f))
                }
                Text(
                    text = "Scrobbling since ${user.user.registered.asParsedDate}",
                    modifier = Modifier.alpha(0.55f)
                )
            }
            Divider(modifier = Modifier.run { padding(vertical = 20.dp) })
            StatisticRow(
                short = false,
                stringResource(R.string.scrobbles) to user.user.scrobbles.toLong(),
                stringResource(R.string.artists) to user.user.artistCount?.toLongOrNull(),
                stringResource(R.string.albums) to user.user.albumCount?.toLongOrNull()
            )
            Divider(modifier = Modifier.run { padding(vertical = 20.dp) })
            Text(
                text = stringResource(R.string.recent_scrobbles),
                style = Heading4,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
            recentScrobbles?.let { track ->
                track.forEach {
                    TrackRow(track = it, nav = nav, favoriteIcon = false)
                }
            }
            Divider(modifier = Modifier.run { padding(vertical = 20.dp) })
            Text(
                text = stringResource(R.string.top_artists),
                style = Heading4,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
            Text(
                text = "Last 30 days",
                style = Subtitle1,
                modifier = Modifier
                    .alpha(0.55f)
                    .padding(start = 20.dp)
                    .fillMaxWidth()
            )
            topArtists?.topArtists?.let {
                TopArtistsRow(artists = it.artists)
            }

            /* TOP ALBUMS */
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.top_albums),
                style = Heading4,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
            Text(
                text = "Last 30 days",
                style = Subtitle1,
                modifier = Modifier
                    .alpha(0.55f)
                    .padding(start = 20.dp)
                    .fillMaxWidth()
            )
            topAlbums?.let {
                TopAlbumsRow(albums = it.topAlbums.albums, nav)
            }
        }
    }
}