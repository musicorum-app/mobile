package io.musicorum.mobile.views.mostListened

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import io.musicorum.mobile.LocalAnalytics
import io.musicorum.mobile.LocalNavigation
import io.musicorum.mobile.components.TrackListItem
import io.musicorum.mobile.ui.theme.LighterGray
import io.musicorum.mobile.viewmodels.MostListenedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MostListened(viewModel: MostListenedViewModel = viewModel()) {
    val analytics = LocalAnalytics.current!!
    LaunchedEffect(Unit) {
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, "most_listened")
        }
    }

    val mostListened by viewModel.mosListenedTracks.observeAsState(emptyList())
    val snackHost = remember { SnackbarHostState() }
    val nav = LocalNavigation.current!!


    LaunchedEffect(viewModel.error.value) {
        if (viewModel.error.value == true) {
            snackHost.showSnackbar("Failed to fetch")
        }
    }

    val state = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val appBarColors = TopAppBarDefaults.topAppBarColors(
        scrolledContainerColor = LighterGray,
    )
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text(text = "Most listened tracks") },
                scrollBehavior = scrollBehavior,
                colors = appBarColors,
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                    }
                }
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackHost) }
    ) {
        if (viewModel.job.isActive) {
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
                modifier = Modifier
                    .padding(it),
            ) {
                items(mostListened) { track ->
                    TrackListItem(track = track)
                }
            }
        }
    }
}
