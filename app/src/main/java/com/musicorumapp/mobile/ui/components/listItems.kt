package com.musicorumapp.mobile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState
import com.musicorumapp.mobile.api.models.*
import com.musicorumapp.mobile.ui.theme.MusicorumTheme
import com.musicorumapp.mobile.ui.theme.SecondaryTextColor

private val roundedImageClip = RoundedCornerShape(4.dp)

@Composable
fun ListItem(
    title: String,
    modifier: Modifier = Modifier,
    subTitle: String? = null,
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
                modifier = Modifier.height(20.dp)
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
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ArtistListItem(
    artist: Artist,
    modifier: Modifier = Modifier
) {

    val painter = rememberCoilPainter(
        artist.imageURL,
        previewPlaceholder = LastfmEntity.ARTIST.asDrawableSource(),
        fadeIn = true,
    )

    artist.onResourcesChange { painter.request = it.imageURL }

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

                when (painter.loadState) {
                    is ImageLoadState.Success -> {}
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

    val painter = rememberCoilPainter(
        album.getImageURL(),
        previewPlaceholder = LastfmEntity.ALBUM.asDrawableSource(),
        fadeIn = true,
    )

    album.onResourcesChange { painter.request = it.getImageURL() }

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
    track: Track,
    modifier: Modifier = Modifier
) {

    val painter = rememberCoilPainter(
        track.getImageURL(),
        previewPlaceholder = LastfmEntity.ALBUM.asDrawableSource(),
        fadeIn = true,
    )

    track.onResourcesChange {
        println("BEST: " + it.getImageURL())
        painter.request = it.getImageURL() }

    ListItem(
        modifier = modifier,
        title = track.name,
        subTitle = track.artist,
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

                Image(painter = painter, contentDescription = track.name)

                when (painter.loadState) {
                    is ImageLoadState.Success -> {}
                    else -> {
                        Image(
                            painter = painterResource(id = LastfmEntity.TRACK.asDrawableSource()),
                            contentDescription = track.name
                        )
                    }
                }
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

@Preview(showBackground = true)
@Composable
fun ListItemPreview() {
    MusicorumTheme {
        Scaffold(
            modifier = Modifier.height(200.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                ArtistListItem(artist = Artist.fromSample(), modifier = Modifier.clickable {  })
                Divider()
                TrackListItem(track = Track.fromSample(), modifier = Modifier.clickable {  })
            }
        }
    }
}