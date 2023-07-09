package io.musicorum.mobile.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import io.musicorum.mobile.LocalAnalytics
import io.musicorum.mobile.LocalNavigation
import io.musicorum.mobile.LocalUser
import io.musicorum.mobile.components.CenteredLoadingSpinner
import io.musicorum.mobile.components.TrackItem
import io.musicorum.mobile.ui.theme.LighterGray
import io.musicorum.mobile.viewmodels.RecentSrcobblesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentScrobbles(
    recentSrcobblesViewModel: RecentSrcobblesViewModel = viewModel(),
) {
    val nav = LocalNavigation.current!!
    val analytics = LocalAnalytics.current!!
    LaunchedEffect(Unit) {
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, "recent_scrobbles")
        }
    }
    val user = LocalUser.current!!
    val recentTracks = recentSrcobblesViewModel.fetchRecentTracks(user.user.name)
        .collectAsLazyPagingItems()
    val state = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val appBarColors = TopAppBarDefaults.topAppBarColors(
        scrolledContainerColor = LighterGray
    )
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text(text = "Recent Scrobbles") },
                scrollBehavior = scrollBehavior,
                colors = appBarColors,
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Rounded.ArrowBack, null)
                    }
                }
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            if (recentTracks.loadState.refresh == LoadState.Loading) {
                CenteredLoadingSpinner()
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(0.dp), state = state) {
                    items(
                        count = recentTracks.itemCount
                    ) { index ->
                        TrackItem(track = recentTracks[index])
                    }
                    item {
                        when (recentTracks.loadState.append) {
                            LoadState.Loading -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(35.dp))
                                }
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }
}