package io.musicorum.mobile.screens.individual

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.palette.graphics.Palette
import io.musicorum.mobile.LocalUser
import io.musicorum.mobile.R
import io.musicorum.mobile.coil.PlaceholderType
import io.musicorum.mobile.components.*
import io.musicorum.mobile.ui.theme.*
import io.musicorum.mobile.utils.LocalSnackbar
import io.musicorum.mobile.utils.createPalette
import io.musicorum.mobile.utils.getBitmap
import io.musicorum.mobile.viewmodels.AlbumViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Album(
    albumData: String?,
    albumViewModel: AlbumViewModel = viewModel(),
    nav: NavHostController
) {
    if (albumData == null) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(AlmostBlack)
        ) {
            Text(text = "Something went wrong")
        }
    } else {
        val partialAlbum = Json.decodeFromString<PartialAlbum>(albumData)
        val album = albumViewModel.album.observeAsState().value?.album
        val artistImage = albumViewModel.artistImage.observeAsState().value
        val ctx = LocalContext.current
        val paletteReady = remember { mutableStateOf(false) }
        val palette: MutableState<Palette?> = remember { mutableStateOf(null) }
        val user = LocalUser.current
        val errored = albumViewModel.errored.observeAsState().value
        val localSnack = LocalSnackbar.current

        LaunchedEffect(key1 = errored) {
            if (errored == true) {
                localSnack.showSnackbar("Failed to fetch album")
            }
        }

        LaunchedEffect(album) {
            if (album == null) {
                albumViewModel.getAlbum(partialAlbum.name, partialAlbum.artist, user)
            }
        }

        if (album == null) {
            CenteredLoadingSpinner()
        } else {
            val scrollState = rememberScrollState()
            LaunchedEffect(Unit) {
                val albumImgBmp = getBitmap(album.bestImageUrl, ctx)
                palette.value = createPalette(albumImgBmp)
                paletteReady.value = true
            }
            val appBarState = rememberTopAppBarState(initialContentOffset = 700f)
            val appBarBehavior =
                TopAppBarDefaults.pinnedScrollBehavior(state = appBarState)

            Scaffold(topBar = {
                MusicorumTopBar(
                    text = album.name,
                    scrollBehavior = appBarBehavior,
                    fadeable = true,
                ) {}
            }) {
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .fillMaxSize()
                        .background(AlmostBlack)
                ) {
                    GradientHeader(
                        artistImage,
                        album.bestImageUrl,
                        RoundedCornerShape(12.dp),
                        PlaceholderType.ALBUM
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        album.name,
                        style = Typography.displaySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 55.dp),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        album.artist ?: "Unknown",
                        style = Typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = ContentSecondary
                    )

                    Divider(Modifier.padding(vertical = 20.dp))

                    StatisticRow(
                        true,
                        stringResource(R.string.listeners) to album.listeners?.toLong(),
                        stringResource(R.string.scrobbles) to album.playCount?.toLong(),
                        stringResource(R.string.your_scrobbles) to album.userPlayCount?.toLong()
                    )

                    album.tags?.let {
                        Spacer(Modifier.height(20.dp))
                        TagList(
                            tags = it.tags,
                            referencePalette = palette.value,
                            visible = !paletteReady.value
                        )
                    }

                    album.wiki?.let {
                        Spacer(Modifier.height(20.dp))
                        Row(modifier = Modifier.padding(horizontal = 20.dp)) {
                            ItemInformation(palette = palette.value, info = it.summary)
                        }
                    }

                    Divider(Modifier.padding(vertical = 20.dp))

                    ContextRow(appearsOn = null, from = Pair(album.artist, artistImage), null)

                    album.tracks?.tracks?.let {
                        Divider(Modifier.padding(vertical = 20.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clickable { nav.navigate("albumTracklist") }
                        ) {
                            Text(
                                text = stringResource(id = R.string.tracks),
                                style = Heading4,
                                modifier = Modifier.padding(start = 20.dp)
                            )
                            Icon(
                                Icons.Rounded.ChevronRight,
                                null,
                                modifier = Modifier.padding(end = 20.dp)
                            )
                        }
                        Text(
                            text = pluralStringResource(
                                id = R.plurals.tracks_quantity,
                                count = it.size,
                                it.size
                            ),
                            modifier = Modifier.padding(start = 20.dp),
                            style = Subtitle1
                        )

                        it.take(4).forEachIndexed { i, track ->
                            AlbumTrack(i + 1, track.name)
                        }
                    }
                    Spacer(Modifier.width(20.dp))
                }
            }
        }
    }
}

@Serializable
data class PartialAlbum(
    val name: String,
    val artist: String
)
