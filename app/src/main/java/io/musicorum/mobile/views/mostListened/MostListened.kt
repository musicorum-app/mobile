package io.musicorum.mobile.views.mostListened

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import io.musicorum.mobile.LocalAnalytics
import io.musicorum.mobile.LocalNavigation
import io.musicorum.mobile.LocalUser
import io.musicorum.mobile.components.TrackItem
import io.musicorum.mobile.models.FetchPeriod
import io.musicorum.mobile.ui.theme.LighterGray
import io.musicorum.mobile.utils.LocalSnackbar
import io.musicorum.mobile.viewmodels.MostListenedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MostListened(mostListenedViewModel: MostListenedViewModel) {
    val analytics = LocalAnalytics.current!!
    LaunchedEffect(Unit) {
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, "most_listened")
        }
    }

    val mostListened = mostListenedViewModel.mosListenedTracks.observeAsState()
    val snack = LocalSnackbar.current
    val user = LocalUser.current!!
    val nav = LocalNavigation.current!!
    LaunchedEffect(Unit) {
        if (mostListenedViewModel.mosListenedTracks.value == null) {
            mostListenedViewModel.fetchMostListened(user.user.name, FetchPeriod.WEEK, null)
        }
    }

    LaunchedEffect(mostListenedViewModel.error.value) {
        if (mostListenedViewModel.error.value == true) {
            snack.showSnackbar("Failed to fetch")
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
                        Icon(Icons.Rounded.ArrowBack, null)
                    }
                }
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
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
                modifier = Modifier
                    .padding(it),
            ) {
                items(mostListened.value!!.topTracks.tracks) { track ->
                    TrackItem(track = track)
                }
            }
        }
    }
}
