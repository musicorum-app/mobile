package io.musicorum.mobile.screens.individual

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.palette.graphics.Palette
import io.musicorum.mobile.LocalUser
import io.musicorum.mobile.R
import io.musicorum.mobile.coil.PlaceholderType
import io.musicorum.mobile.components.*
import io.musicorum.mobile.ui.theme.AlmostBlack
import io.musicorum.mobile.ui.theme.Typography
import io.musicorum.mobile.utils.createPalette
import io.musicorum.mobile.utils.getBitmap
import io.musicorum.mobile.viewmodels.ArtistViewModel

@Composable
fun Artist(artistName: String, artistViewModel: ArtistViewModel = viewModel()) {
    val artist = artistViewModel.artist.observeAsState().value
    val topAlbumImage = artistViewModel.topAlbumImage.observeAsState().value
    val user = LocalUser.current!!.user
    val palette = remember { mutableStateOf<Palette?>(null) }
    val paletteReady = remember { mutableStateOf(false) }
    val ctx = LocalContext.current

    LaunchedEffect(artist) {
        if (artist == null) {
            artistViewModel.fetchArtist(artistName, user.name)
        } else {
            val bmp = getBitmap(artist.bestImageUrl, ctx)
            val p = createPalette(bmp)
            palette.value = p
            paletteReady.value = true
        }
    }

    if (artist == null) {
        CenteredLoadingSpinner()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AlmostBlack)
        ) {
            GradientHeader(
                backgroundUrl = topAlbumImage,
                coverUrl = artist.bestImageUrl,
                shape = CircleShape,
                placeholderType = PlaceholderType.ARTIST
            )
            Text(
                text = artist.name,
                style = Typography.displaySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Divider(modifier = Modifier.padding(vertical = 18.dp))
            StatisticRow(
                short = true,
                stringResource(id = R.string.listeners) to artist.stats?.listeners,
                stringResource(id = R.string.scrobbles) to artist.stats?.playCount,
                stringResource(id = R.string.your_scrobbles) to artist.stats?.userPlayCount
            )
            Spacer(modifier = Modifier.height(20.dp))

            artist.tags?.let {
                TagList(
                    tags = it.tags,
                    referencePalette = palette.value,
                    visible = !paletteReady.value
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            artist.bio?.let {
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    ItemInformation(palette = palette.value, info = it.summary)
                }
            }
        }
    }
}