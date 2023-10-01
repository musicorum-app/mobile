package io.musicorum.mobile.views.individual

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.musicorum.mobile.R
import io.musicorum.mobile.coil.PlaceholderType
import io.musicorum.mobile.components.AlbumCard
import io.musicorum.mobile.components.ArtistRow
import io.musicorum.mobile.components.CenteredLoadingSpinner
import io.musicorum.mobile.components.GradientHeader
import io.musicorum.mobile.components.ItemInformation
import io.musicorum.mobile.components.TrackListItem
import io.musicorum.mobile.ui.theme.ContentSecondary
import io.musicorum.mobile.ui.theme.KindaBlack
import io.musicorum.mobile.ui.theme.Typography
import io.musicorum.mobile.viewmodels.TagViewmodel

@Composable
fun TagScreen(viewModel: TagViewmodel = viewModel()) {
    val tagInfo by viewModel.tagInfo.observeAsState(null)
    val tagAlbums by viewModel.tagAlbums.observeAsState()
    val palette by viewModel.imagePalette.observeAsState(null)
    val tracks by viewModel.tracks.observeAsState(null)
    val topArtists by viewModel.topArtists.observeAsState(null)

    if (tagInfo == null || tracks == null || topArtists == null) {
        CenteredLoadingSpinner()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KindaBlack)
            .verticalScroll(rememberScrollState())
    ) {
        GradientHeader(
            backgroundUrl = topArtists?.get(0)?.bestImageUrl,
            coverUrl = null,
            showCover = false,
            shape = CircleShape,
            placeholderType = PlaceholderType.ARTIST
        )
        Text(
            text = tagInfo!!.name,
            modifier = Modifier
                .align(CenterHorizontally)
                .offset(y = (-30).dp),
            style = Typography.displaySmall
        )


        tagInfo!!.wiki?.let {
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                ItemInformation(palette = palette, info = it.summary)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
        HorizontalDivider()
        ListItem(
            headlineContent = {
                Text(
                    stringResource(id = R.string.top_artists),
                    style = Typography.headlineSmall
                )
            },
            supportingContent = {
                Text(
                    pluralStringResource(
                        id = R.plurals.artists_quantity,
                        count = topArtists?.size ?: 0,
                        topArtists?.size ?: 0
                    ),
                    style = Typography.bodyMedium,
                    color = ContentSecondary
                )
            }
        )
        topArtists?.let {
            ArtistRow(artists = it)
        }

        Spacer(modifier = Modifier.height(35.dp))
        ListItem(
            headlineContent = {
                Text(
                    stringResource(id = R.string.top_tracks),
                    style = Typography.headlineSmall
                )
            },
            supportingContent = {
                Text(
                    pluralStringResource(
                        id = R.plurals.tracks_quantity,
                        count = tracks?.size ?: 0,
                        tracks?.size ?: 0
                    ),
                    style = Typography.bodyMedium,
                    color = ContentSecondary
                )
            }
        )


        val t = tracks?.take(4)
        t?.let { trackList ->
            trackList.forEach {
                TrackListItem(track = it, favoriteIcon = false)
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        ListItem(
            headlineContent = {
                Text(
                    stringResource(id = R.string.top_albums),
                    style = Typography.headlineSmall
                )
            },
            supportingContent = {
                Text(
                    pluralStringResource(
                        id = R.plurals.albums_quantity,
                        count = tagAlbums?.size ?: 0,
                        tagAlbums?.size ?: 0
                    ),
                    style = Typography.bodyMedium,
                    color = ContentSecondary
                )
            }
        )

        tagAlbums?.let {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                item {
                    Spacer(modifier = Modifier.width(5.dp))
                }
                items(it) {
                    AlbumCard(album = it)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}