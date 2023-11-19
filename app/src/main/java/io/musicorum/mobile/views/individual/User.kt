package io.musicorum.mobile.views.individual

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.musicorum.mobile.R
import io.musicorum.mobile.coil.PlaceholderType
import io.musicorum.mobile.components.CenteredLoadingSpinner
import io.musicorum.mobile.components.GradientHeader
import io.musicorum.mobile.components.Section
import io.musicorum.mobile.components.StatisticRow
import io.musicorum.mobile.components.TopAlbumsRow
import io.musicorum.mobile.components.TopArtistsRow
import io.musicorum.mobile.components.TrackListItem
import io.musicorum.mobile.components.skeletons.GenericListItemSkeleton
import io.musicorum.mobile.ui.theme.ContentSecondary
import io.musicorum.mobile.ui.theme.Heading4
import io.musicorum.mobile.ui.theme.KindaBlack
import io.musicorum.mobile.ui.theme.Subtitle1
import io.musicorum.mobile.ui.theme.Typography
import io.musicorum.mobile.utils.LocalSnackbar
import io.musicorum.mobile.viewmodels.UserViewModel

@Composable
fun User(
    userViewModel: UserViewModel = viewModel()
) {
    val user by userViewModel.user.observeAsState()

    val topArtists by userViewModel.topArtists.observeAsState()
    val recentScrobbles = userViewModel.recentTracks.observeAsState().value?.recentTracks?.tracks
    val topAlbums by userViewModel.topAlbums.observeAsState()
    val isRefreshing =
        rememberSwipeRefreshState(isRefreshing = userViewModel.isRefreshing.collectAsState().value)
    val errored by userViewModel.errored.observeAsState()
    val scrollState = rememberScrollState()
    val localSnack = LocalSnackbar.current

    LaunchedEffect(errored) {
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
                    .background(KindaBlack)
                    .padding(bottom = 20.dp)
            ) {
                GradientHeader(
                    backgroundUrl = topArtists?.topArtists?.artists?.getOrNull(0)?.bestImageUrl,
                    coverUrl = user?.user?.bestImageUrl,
                    shape = CircleShape,
                    placeholderType = PlaceholderType.USER
                )

                Text(text = user?.user!!.name, style = Typography.displaySmall)
                Row {
                    user?.user!!.realName?.let {
                        Text(
                            text = "$it • ",
                            color = ContentSecondary,
                            style = Typography.bodyLarge
                        )
                    }
                    Text(
                        text = "Scrobbling since ${user?.user?.registered?.asParsedDate}",
                        style = Typography.bodyLarge,
                        color = ContentSecondary
                    )
                }

                HorizontalDivider(modifier = Modifier.run { padding(vertical = 20.dp) })
                StatisticRow(
                    short = false,
                    stringResource(R.string.scrobbles) to user?.user?.scrobbles?.toLong(),
                    stringResource(R.string.artists) to user?.user?.artistCount?.toLongOrNull(),
                    stringResource(R.string.albums) to user?.user?.albumCount?.toLongOrNull()
                )

                HorizontalDivider(modifier = Modifier.run { padding(vertical = 20.dp) })
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
                            TrackListItem(
                                track = it,
                                favoriteIcon = false,
                                showTimespan = true
                            )
                        }
                    }
                }

                /* TOP ARTISTS */
                HorizontalDivider(modifier = Modifier.run { padding(vertical = 20.dp) })
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
                        style = Subtitle1,
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