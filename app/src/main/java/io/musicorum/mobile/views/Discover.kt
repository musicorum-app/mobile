package io.musicorum.mobile.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.rounded.Audiotrack
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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.musicorum.mobile.R
import io.musicorum.mobile.components.AlbumListItem
import io.musicorum.mobile.components.ArtistListItem
import io.musicorum.mobile.components.CenteredLoadingSpinner
import io.musicorum.mobile.components.TrackListItem
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


    Column(
        modifier = Modifier
            .padding(vertical = 20.dp)
            .fillMaxSize()
            .background(KindaBlack)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            stringResource(R.string.discover),
            style = Typography.displaySmall,
            modifier = Modifier.padding(start = 20.dp)
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
            placeholder = { Text(stringResource(R.string.search_on_last_fm)) },
            leadingIcon = { Icon(Icons.Rounded.Search, null) }
        ) {}

        if (!presentResults.value) return

        if (busy) {
            CenteredLoadingSpinner()
            return
        }

        Header(
            title = stringResource(R.string.tracks),
            results = tracks.size,
            icon = Icons.Rounded.Audiotrack
        )
        if (tracks.isEmpty()) {
            Text(stringResource(R.string.no_results))
        } else {
            tracks.take(4).forEach {
                TrackListItem(track = it)
            }
        }

        Header(
            title = stringResource(id = R.string.albums),
            results = albums.size,
            icon = Icons.Outlined.Album
        )
        if (albums.isEmpty()) {
            Text(stringResource(R.string.no_results))
        } else {
            albums.take(4).forEach {
                AlbumListItem(it)
            }
        }

        Header(
            title = stringResource(id = R.string.artists),
            results = artists.size,
            icon = Icons.Rounded.Star
        )
        if (artists.isEmpty()) {
            Text(stringResource(R.string.no_results))
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
                text = pluralStringResource(
                    id = R.plurals.search_result_quantity,
                    count = results,
                    results
                ),
                style = Typography.bodyMedium,
                color = ContentSecondary
            )
        },
        leadingContent = { Icon(icon, null, tint = Color.White) },
        //trailingContent = { Icon(Icons.Rounded.ChevronRight, null, tint = Color.White) }
        //TODO: individual pages on chevron click
    )
}
