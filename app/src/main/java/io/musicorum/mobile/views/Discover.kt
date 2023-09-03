package io.musicorum.mobile.views

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.rounded.Audiotrack
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.musicorum.mobile.components.AlbumListItem
import io.musicorum.mobile.components.ArtistListItem
import io.musicorum.mobile.components.CenteredLoadingSpinner
import io.musicorum.mobile.components.TrackItem
import io.musicorum.mobile.ui.theme.ContentSecondary
import io.musicorum.mobile.ui.theme.KindaBlack
import io.musicorum.mobile.ui.theme.LighterGray
import io.musicorum.mobile.ui.theme.Typography
import io.musicorum.mobile.viewmodels.DiscoverVm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Discover(viewModel: DiscoverVm = viewModel()) {
    val query = viewModel.query.observeAsState("").value
    val presentResults = rememberSaveable { mutableStateOf(false) }
    val artists = viewModel.artistResults.observeAsState(emptyList()).value
    val tracks = viewModel.trackResults.observeAsState(emptyList()).value
    val albums = viewModel.albumResults.observeAsState(emptyList()).value
    val searchBarColors = SearchBarDefaults.colors(
        containerColor = LighterGray
    )
    val busy = viewModel.busy.observeAsState(false).value


    Column(modifier = Modifier
        .padding(vertical = 20.dp)
        .fillMaxSize()
        .background(KindaBlack)
        .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Discover",
            style = Typography.displaySmall,
            modifier = Modifier.padding(start = 15.dp)
        )

        DockedSearchBar(
            query = query,
            onQueryChange = { viewModel.updateQuery(it) },
            onSearch = {
                viewModel.search()
                presentResults.value = true
            },
            active = false,
            onActiveChange = {},
            colors = searchBarColors,
            modifier = Modifier
                .padding(top = 10.dp)
                .align(CenterHorizontally),
            placeholder = { Text("Search on Last.fm...") },
            leadingIcon = { Icon(Icons.Rounded.Search, null) }
        ) {}

        if (!presentResults.value) return

        if (busy) {
            CenteredLoadingSpinner()
            return
        }

        Header(title = "Tracks", results = tracks.size, icon = Icons.Rounded.Audiotrack)
        if (tracks.isEmpty()) {
            Text("No results")
        } else {
            tracks.take(4).forEach {
                TrackItem(track = it)
            }
        }

        Header(title = "Albums", results = albums.size, icon = Icons.Outlined.Album)
        if (albums.isEmpty()) {
            Text("No results")
        } else {
            albums.take(4).forEach {
                AlbumListItem(it)
            }
        }

        Header(title = "Artists", results = artists.size, icon = Icons.Rounded.Star)
        if (artists.isEmpty()) {
            Text("No results")
        } else {
            artists.take(4).forEach {
                ArtistListItem(artist = it)
            }
        }
    }
}

@Composable
private fun Header(title: String, results: Int, icon: ImageVector) {
    ListItem(
        headlineContent = { Text(title, style = Typography.headlineSmall) },
        supportingContent = {
            Text(
                text = "$results results",
                style = Typography.bodyMedium,
                color = ContentSecondary
            )
        },
        leadingContent = { Icon(icon, null, tint = Color.White) },
        //trailingContent = { Icon(Icons.Rounded.ChevronRight, null, tint = Color.White) }
        //TODO: individual pages on chevron click
    )
}
