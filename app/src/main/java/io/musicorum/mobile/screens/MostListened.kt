package io.musicorum.mobile.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import io.musicorum.mobile.LocalUser
import io.musicorum.mobile.R
import io.musicorum.mobile.components.MusicorumTopBar
import io.musicorum.mobile.components.TrackItem
import io.musicorum.mobile.ktor.endpoints.FetchPeriod
import io.musicorum.mobile.viewmodels.MostListenedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MostListened(
    mostListenedViewModel: MostListenedViewModel,
    nav: NavHostController
) {
    val mostListened = mostListenedViewModel.mosListenedTracks.observeAsState()
    val user = LocalUser.current!!
    LaunchedEffect(Unit) {
        if (mostListenedViewModel.mosListenedTracks.value == null) {
            mostListenedViewModel.fetchMostListened(user.user.name, FetchPeriod.WEEK, null)
        }
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            MusicorumTopBar(
                text = stringResource(R.string.most_listened_tracks),
                scrollBehavior = scrollBehavior,
                fadeable = false
            ) {}
        }
    ) {
        if (mostListened.value == null) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier
                    .padding(it)
            ) {
                items(mostListened.value!!.topTracks.tracks) { track ->
                    TrackItem(track = track)
                }
            }
        }

    }
}
