package io.musicorum.mobile.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.musicorum.mobile.components.MusicorumTopBar
import io.musicorum.mobile.components.TrackRow
import io.musicorum.mobile.ui.theme.LightGray
import io.musicorum.mobile.viewmodels.HomeViewModel
import io.musicorum.mobile.viewmodels.RecentSrcobblesViewModel
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentScrobbles(
    homeViewModel: HomeViewModel,
    recentSrcobblesViewModel: RecentSrcobblesViewModel = viewModel(),
    nav: NavHostController
) {
    val recentTracks = recentSrcobblesViewModel.recentTracks.observeAsState()
    LaunchedEffect(key1 = recentSrcobblesViewModel) {
        if (recentSrcobblesViewModel.recentTracks.value == null) {
            recentSrcobblesViewModel.fetchRecentTracks(
                homeViewModel.user.value!!.user.name,
                "${Instant.now().minusSeconds(604800).toEpochMilli() / 1000}",
                null,
                true
            )
        }
    }
    val state = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            MusicorumTopBar(
                text = "Recent Scrobbles",
                scrollBehavior = scrollBehavior,
                nav = nav
            )
        },
        modifier = Modifier
            .background(LightGray)
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        Column(
            modifier = Modifier
                .padding(it)
        ) {
            if (recentTracks.value == null) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val tracks = recentTracks.value!!.tracks
                LazyColumn(verticalArrangement = Arrangement.spacedBy(0.dp), state = state) {
                    items(tracks) { track ->
                        TrackRow(track = track, nav)
                    }
                }
            }
        }
    }
}