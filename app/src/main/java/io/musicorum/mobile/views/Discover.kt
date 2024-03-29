package io.musicorum.mobile.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.rounded.Audiotrack
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import io.musicorum.mobile.LocalNavigation
import io.musicorum.mobile.R
import io.musicorum.mobile.coil.PlaceholderType
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.components.AlbumListItem
import io.musicorum.mobile.components.ArtistListItem
import io.musicorum.mobile.components.CenteredLoadingSpinner
import io.musicorum.mobile.components.TrackListItem
import io.musicorum.mobile.router.BottomNavBar
import io.musicorum.mobile.router.Routes
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
    val users by viewModel.userResult.observeAsState(emptyList())
    val searchBarColors = SearchBarDefaults.colors(
        containerColor = LighterGray
    )
    val busy = viewModel.busy.observeAsState(false).value
    val nav = LocalNavigation.current
    val keyboard = LocalSoftwareKeyboardController.current


    Scaffold(bottomBar = { BottomNavBar() }) { pv ->
        Column(
            modifier = Modifier
                .padding(vertical = 20.dp)
                .padding(pv)
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
                    keyboard?.hide()
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

            if (!presentResults.value) return@Column

            if (busy) {
                Box(
                    modifier = Modifier
                        .height(IntrinsicSize.Max)
                        .padding(top = 0.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CenteredLoadingSpinner()
                }
                return@Scaffold
            }

            Header(
                title = stringResource(R.string.tracks),
                results = tracks.size,
                icon = Icons.Rounded.Audiotrack
            )

            if (tracks.isNotEmpty()) {
                tracks.take(4).forEach {
                    TrackListItem(track = it)
                }
            }

            Header(
                title = stringResource(id = R.string.albums),
                results = albums.size,
                icon = Icons.Outlined.Album
            )
            if (albums.isNotEmpty()) {
                albums.take(4).forEach {
                    AlbumListItem(it)
                }
            }

            Header(
                title = stringResource(id = R.string.artists),
                results = artists.size,
                icon = Icons.Rounded.Star
            )
            if (artists.isNotEmpty()) {
                artists.take(4).forEach {
                    ArtistListItem(artist = it)
                }
            }

            Header(
                title = "Users",
                results = users.size,
                icon = Icons.Rounded.Person
            )
            if (users.isNotEmpty()) {
                val model = defaultImageRequestBuilder(
                    url = users.first().user.bestImageUrl,
                    PlaceholderType.USER
                )
                ListItem(
                    headlineContent = { Text(users.first().user.name) },
                    leadingContent = {
                        AsyncImage(
                            model = model,
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                        )
                    },
                    modifier = Modifier.clickable {
                        nav?.navigate(Routes.user(users.first().user.name))
                    }
                )
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
