package io.musicorum.mobile.components

import android.app.SearchManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import io.musicorum.mobile.LocalNavigation
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.ktor.endpoints.TrackEndpoint
import io.musicorum.mobile.router.Routes
import io.musicorum.mobile.serialization.entities.Track
import io.musicorum.mobile.ui.theme.ContentSecondary
import io.musicorum.mobile.ui.theme.EvenLighterGray
import io.musicorum.mobile.ui.theme.LighterGray
import io.musicorum.mobile.ui.theme.Typography
import io.musicorum.mobile.views.individual.PartialAlbum
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackSheet(
    track: Track,
    show: MutableState<Boolean>,
    additionalSheetItems: (@Composable () -> Unit)? = null
) {
    val coroutine = rememberCoroutineScope()
    val trackLoved = rememberSaveable { mutableStateOf(track.loved) }
    val ctx = LocalContext.current
    val nav = LocalNavigation.current
    val listColors = ListItemDefaults.colors(
        containerColor = LighterGray
    )
    val sheetState = rememberModalBottomSheetState()
    val spotifyIntent = ctx.packageManager.getLaunchIntentForPackage("com.spotify.music")
        ?.setAction(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH)
        ?.putExtra(SearchManager.QUERY, "${track.artist.name} ${track.name}")

    ModalBottomSheet(
        onDismissRequest = { show.value = false },
        containerColor = LighterGray,
        sheetState = sheetState,

        ) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = defaultImageRequestBuilder(url = track.bestImageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .size(80.dp)
                )

                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text(
                        text = track.name,
                        style = Typography.headlineSmall,
                        maxLines = 1,
                        modifier = Modifier.width(210.dp),
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = track.artist.name,
                        style = Typography.bodyMedium,
                        color = ContentSecondary,
                        maxLines = 1,
                    )
                    /*
                    track.date?.uts?.let {
                        val time = DateUtils.getRelativeTimeSpanString(
                            it.toLong() * 1000,
                            System.currentTimeMillis(),
                            DateUtils.SECOND_IN_MILLIS
                        ).toString()
                        Text(text = time)
                    }*/
                }
            }
            IconToggleButton(
                checked = trackLoved.value,
                onCheckedChange = {
                    trackLoved.value = it
                    coroutine.launch {
                        TrackEndpoint.updateFavoritePreference(track, !it, ctx)
                    }
                },
            ) {
                if (trackLoved.value) {
                    Icon(Icons.Rounded.Favorite, null)
                } else {
                    Icon(Icons.Rounded.FavoriteBorder, null)
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 20.dp, end = 20.dp),
            color = EvenLighterGray
        )

        additionalSheetItems?.let {
            it()
        }

        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    show.value = false
                    nav?.navigate(Routes.artist(track.artist.name))
                },
            headlineContent = { Text(text = "Go to artist") },
            leadingContent = { Icon(Icons.Rounded.Star, null) },
            colors = listColors
        )

        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    track.album?.let {
                        show.value = false
                        val partialAlbum = PartialAlbum(it.name, track.artist.name)
                        nav?.navigate(Routes.album(Json.encodeToString(partialAlbum)))
                    }
                },
            headlineContent = { Text(text = "Go to album") },
            colors = listColors,
            leadingContent = { Icon(Icons.Rounded.Album, null) }
        )

        val spotify = try {
            ctx.packageManager.getApplicationIcon("com.spotify.music")
                .toBitmap()
                .asImageBitmap()
        } catch (_: PackageManager.NameNotFoundException) {
            null
        }

        spotify?.let {
            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { ctx.startActivity(spotifyIntent) },
                headlineContent = { Text(text = "Play on Spotify") },
                leadingContent = {
                    Image(
                        bitmap = it,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                colors = listColors
            )
        }

        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(track.url))
                    ctx.startActivity(intent)
                },
            headlineContent = { Text(text = "Open on Last.fm") },
            leadingContent = { Icon(Icons.AutoMirrored.Rounded.OpenInNew, null) },
            colors = listColors
        )
        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {},
            headlineContent = { Text(text = "Share") },
            leadingContent = { Icon(Icons.Rounded.Share, null) },
            colors = listColors
        )
        Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
    }
}