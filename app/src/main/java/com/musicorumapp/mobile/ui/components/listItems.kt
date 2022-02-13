package com.musicorumapp.mobile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.musicorumapp.mobile.Constants
import com.musicorumapp.mobile.api.models.*
import com.musicorumapp.mobile.ui.theme.MusicorumTheme
import com.musicorumapp.mobile.ui.theme.SecondaryTextColor

private val roundedImageClip = RoundedCornerShape(4.dp)

@Composable
fun ListItem(
    title: String,
    modifier: Modifier = Modifier,
    subTitle: String? = null,
    placeholder: Boolean = false,
    subtitleIcon: @Composable () -> Unit = {},
    leftImage: @Composable () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .composed { modifier }
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        leftImage()

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier
                    .placeholder(
                        visible = placeholder,
                        highlight = PlaceholderHighlight.fade()
                    )
                    .height(20.dp)
            )
            if (subTitle != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    subtitleIcon()

                    Text(
                        subTitle,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        fontSize = 13.sp,
                        color = SecondaryTextColor,
                        modifier = Modifier
                            .placeholder(
                                visible = placeholder,
                                highlight = PlaceholderHighlight.fade()
                            )
                            .padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun ArtistListItem(
    artist: Artist,
    modifier: Modifier = Modifier
) {

    val painter = rememberImagePainter(
        artist.imageURL,
    )

//    artist.onResourcesChange { painter.request = it.imageURL }

    ListItem(
        modifier = modifier,
        title = artist.name,
        leftImage = {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                )

                Image(painter = painter, contentDescription = artist.name)

                when (painter.state) {
                    is ImagePainter.State.Success -> {}
                    else -> {
                        Image(
                            painter = painterResource(id = LastfmEntity.ARTIST.asDrawableSource()),
                            contentDescription = artist.name
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun AlbumListItem(
    album: Album,
    modifier: Modifier = Modifier
) {

    val painter = rememberImagePainter(
        album.imageURL,
//        previewPlaceholder = LastfmEntity.ALBUM.asDrawableSource(),
//        fadeIn = true,
    )

//    album.onResourcesChange { painter.request = it.imageURL }

    ListItem(
        modifier = modifier,
        title = album.name,
        subTitle = album.artist,
        leftImage = {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(roundedImageClip)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                )

                LastfmImageComponent(images = album.images, contentDescription = album.name)
            }
        }
    )
}

@Composable
fun TrackListItem(
    track: Track?,
    modifier: Modifier = Modifier
) {
    val imageURL = remember {
        mutableStateOf(track?.imageURL)
    }

    val painter = rememberImagePainter(
        imageURL.value,
        builder = {
            crossfade(true)
            placeholder(LastfmEntity.TRACK.asDrawableSource())
        }
    )

    LaunchedEffect(track) {
        track?.onResourcesChange {
            imageURL.value = track?.imageURL
        }
    }

    ListItem(
        modifier = modifier,
        title = track?.name.orEmpty(),
        subTitle = track?.artist,
        placeholder = track?.name == null,
        leftImage = {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(roundedImageClip)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                )

                Image(
                    painter = painterResource(id = LastfmEntity.TRACK.asDrawableSource()),
                    contentDescription = track?.name
                )

                Image(painter = painter, contentDescription = track?.name)

            }
        }
    )
}

@Composable
fun UserListItem(
    user: User,
    modifier: Modifier = Modifier
) {

    ListItem(
        modifier = modifier,
        title = user.displayName,
        subTitle = if (user.name != null) "@${user.userName}" else null,
        leftImage = {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                )

                LastfmImageComponent(images = user.images, contentDescription = user.displayName)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ListItemPreview() {
    MusicorumTheme {
        Scaffold (
            modifier = Modifier.height(400.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                ArtistListItem(artist = Artist.fromSample(), modifier = Modifier.clickable {  })
                Divider()
                TrackListItem(track = Track.fromSample(), modifier = Modifier.clickable {  })
                Divider()
                TrackListItem(track = null, modifier = Modifier.clickable {  })
            }
        }
    }
}