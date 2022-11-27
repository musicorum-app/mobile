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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.musicorum.mobile.LocalUser
import io.musicorum.mobile.R
import io.musicorum.mobile.components.MusicorumTopBar
import io.musicorum.mobile.components.TrackItem
import io.musicorum.mobile.ui.theme.EvenLighterGray
import io.musicorum.mobile.viewmodels.RecentSrcobblesViewModel
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentScrobbles(
    recentSrcobblesViewModel: RecentSrcobblesViewModel = viewModel(),
) {
    val recentTracks = recentSrcobblesViewModel.recentTracks.observeAsState()
    val user = LocalUser.current!!
    LaunchedEffect(key1 = recentSrcobblesViewModel) {
        if (recentSrcobblesViewModel.recentTracks.value == null) {
            recentSrcobblesViewModel.fetchRecentTracks(
                user.user.name,
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
                text = stringResource(R.string.recent_scrobbles),
                scrollBehavior = scrollBehavior,
                fadeable = false
            ) {}
        },
        modifier = Modifier
            .background(EvenLighterGray)
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
                        TrackItem(track = track)
                    }
                }
            }
        }
    }
}