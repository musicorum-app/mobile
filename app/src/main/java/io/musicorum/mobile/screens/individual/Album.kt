package io.musicorum.mobile.screens.individual

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.musicorum.mobile.R
import io.musicorum.mobile.components.GradientHeader
import io.musicorum.mobile.components.StatisticRow
import io.musicorum.mobile.ui.theme.AlmostBlack
import io.musicorum.mobile.ui.theme.BodySmall
import io.musicorum.mobile.ui.theme.Heading2
import io.musicorum.mobile.viewmodels.AlbumViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Composable
fun Album(albumData: String?, albumViewModel: AlbumViewModel = viewModel()) {
    if (albumData == null) {
        Row(modifier = Modifier.fillMaxSize().background(AlmostBlack)) {
            Text(text = "Something went wrong")
        }
    } else {
        val partialAlbum = Json.decodeFromString<PartialAlbum>(albumData)
        val album = albumViewModel.album.observeAsState().value?.album
        val artistImage = albumViewModel.artistImage.observeAsState().value

        LaunchedEffect(album) {
            if (album == null) {
                albumViewModel.getAlbum(partialAlbum.name, partialAlbum.artist)
            }
        }

        if (album == null) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) { CircularProgressIndicator() }
        } else {
            Column {
                GradientHeader(artistImage, album.bestImageUrl, RoundedCornerShape(12.dp))
                Text(
                    album.albumName,
                    style = Heading2,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    album.artist ?: "Unknown",
                    style = BodySmall,
                    modifier = Modifier.alpha(0.55f).fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Divider(Modifier.padding(vertical = 20.dp))
                StatisticRow(
                    true,
                    stringResource(R.string.listeners) to album.listeners?.toLong(),
                    stringResource(R.string.scrobbles) to album.playCount?.toLong(),
                    stringResource(R.string.your_scrobbles) to album.userPlayCount?.toLong()
                    )
            }
        }
    }
}

@Serializable
data class PartialAlbum(
    val name: String,
    val artist: String
)
