package io.musicorum.mobile.views.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.musicorum.mobile.LocalNavigation
import io.musicorum.mobile.R
import io.musicorum.mobile.components.TrackListItem
import io.musicorum.mobile.ui.theme.ContentSecondary
import io.musicorum.mobile.ui.theme.Typography
import io.musicorum.mobile.viewmodels.PendingScrobblesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingScrobbles(viewModel: PendingScrobblesViewModel = viewModel()) {
    val lastState by viewModel.lastSyncStatus.observeAsState("")
    val scrobbles by viewModel.pendingScrobbles.observeAsState(emptyList())
    val nav = LocalNavigation.current

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text(stringResource(R.string.pending_scrobbles)) },
                navigationIcon = {
                    IconButton(onClick = { nav?.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                    }
                })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            Text(
                text = stringResource(
                    R.string.last_sync_status,
                    lastState.lowercase().replaceFirstChar { it.uppercase() }),
                style = Typography.labelMedium,
                color = ContentSecondary,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            if (scrobbles.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(R.string.pending_scrobbles_empty),
                        style = Typography.titleLarge,
                        color = ContentSecondary,

                        )
                }
            } else {
                LazyColumn {
                    items(scrobbles) {
                        TrackListItem(track = it.toTrack(), favoriteIcon = false)
                    }
                }
            }
        }
    }
}