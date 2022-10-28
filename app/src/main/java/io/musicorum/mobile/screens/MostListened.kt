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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.musicorum.mobile.components.MusicorumTopBar
import io.musicorum.mobile.components.TrackRow
import io.musicorum.mobile.ktor.endpoints.FetchPeriod
import io.musicorum.mobile.viewmodels.HomeViewModel
import io.musicorum.mobile.viewmodels.MostListenedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MostListened(
    homeViewModel: HomeViewModel,
    mostListenedViewModel: MostListenedViewModel,
    nav: NavHostController
) {
    val mostListened = mostListenedViewModel.mosListenedTracks.observeAsState()
    LaunchedEffect(key1 = mostListenedViewModel) {
        val user = homeViewModel.user.value!!
        if (mostListenedViewModel.mosListenedTracks.value == null) {
            mostListenedViewModel.fetchMostListened(user.user.name, FetchPeriod.WEEK, null)
        }
    }
    val state = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            MusicorumTopBar(
                text = "Most Listened Tracks",
                scrollBehavior = scrollBehavior,
                nav = nav
            )
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
                state = state,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .padding(it)
                    .padding(15.dp)
            ) {
                items(mostListened.value!!.topTracks.tracks) { track ->
                    TrackRow(track = track, nav = nav)
                }
            }
        }

    }
}
