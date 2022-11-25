package io.musicorum.mobile.screens.individual

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.musicorum.mobile.LocalUser
import io.musicorum.mobile.R
import io.musicorum.mobile.coil.PlaceholderType
import io.musicorum.mobile.components.*
import io.musicorum.mobile.components.skeletons.GenericListItemSkeleton
import io.musicorum.mobile.ktor.endpoints.FetchPeriod
import io.musicorum.mobile.ui.theme.*
import io.musicorum.mobile.utils.LocalSnackbar
import io.musicorum.mobile.viewmodels.UserViewModel

@Composable
fun User(
    username: String,
    userViewModel: UserViewModel = viewModel()
) {
    val user = if (username == LocalUser.current?.user?.name) {
        LocalUser.current!!
    } else {
        userViewModel.user.value
    }
    val topArtists = userViewModel.topArtists.observeAsState().value
    val recentScrobbles = userViewModel.recentTracks.observeAsState().value?.recentTracks?.tracks
    val topAlbums = userViewModel.topAlbums.observeAsState().value
    val isRefreshing =
        rememberSwipeRefreshState(isRefreshing = userViewModel.isRefreshing.collectAsState().value)
    val errored = userViewModel.errored.observeAsState().value
    val scrollState = rememberScrollState()
    val localSnack = LocalSnackbar.current

    LaunchedEffect(user, recentScrobbles, errored) {
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
        if (errored == true) {
            localSnack.showSnackbar("Failed to get some information")
        }
    }


    if (user == null) {
        CenteredLoadingSpinner()
    } else {
        SwipeRefresh(state = isRefreshing, onRefresh = { userViewModel.refresh() }) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .background(AlmostBlack)
                    .padding(bottom = 20.dp)
            ) {
                GradientHeader(
                    backgroundUrl = topArtists?.topArtists?.artists?.getOrNull(0)?.bestImageUrl,
                    coverUrl = user.user.bestImageUrl,
                    shape = CircleShape,
                    placeholderType = PlaceholderType.USER
                )

                Text(text = user.user.name, style = Typography.displaySmall)
                Row {
                    user.user.realName?.let {
                        Text(
                            text = "$it • ",
                            color = ContentSecondary,
                            style = Typography.bodyLarge
                        )
                    }
                    Text(
                        text = "Scrobbling since ${user.user.registered.asParsedDate}",
                        style = Typography.bodyLarge,
                        color = ContentSecondary
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
                if (recentScrobbles == null) {
                    GenericListItemSkeleton(visible = true)
                    GenericListItemSkeleton(visible = true)
                    GenericListItemSkeleton(visible = true)

                } else {
                    recentScrobbles.let { track ->
                        track.forEach {
                            TrackItem(
                                track = it,
                                favoriteIcon = false,
                                showTimespan = true
                            )
                        }
                    }
                }

                /* TOP ARTISTS */
                Divider(modifier = Modifier.run { padding(vertical = 20.dp) })
                Section(title = stringResource(id = R.string.top_artists), TextAlign.Start)

                Text(
                    text = stringResource(id = R.string.last_month),
                    style = Typography.bodyMedium,
                    color = ContentSecondary,
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .fillMaxWidth()
                )
                if (topArtists?.topArtists?.artists.isNullOrEmpty()) {
                    Text(
                        text = stringResource(id = R.string.no_data_available),
                        textAlign = TextAlign.Start,
                        style = Typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 20.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(10.dp))
                    TopArtistsRow(artists = topArtists!!.topArtists.artists)
                }

                /* TOP ALBUMS */
                Spacer(modifier = Modifier.height(20.dp))
                Section(title = stringResource(id = R.string.top_albums), TextAlign.Start)
                Text(
                    text = stringResource(id = R.string.last_month),
                    style = Typography.bodyMedium,
                    color = ContentSecondary,
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .fillMaxWidth()
                )
                if (!topAlbums?.topAlbums?.albums.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    TopAlbumsRow(albums = topAlbums!!.topAlbums.albums)
                } else {
                    Text(
                        text = stringResource(id = R.string.no_data_available),
                        textAlign = TextAlign.Start,
                        style = Subtitle1,
                        modifier = Modifier.padding(vertical = 20.dp)
                    )
                }
            }
        }
    }
}