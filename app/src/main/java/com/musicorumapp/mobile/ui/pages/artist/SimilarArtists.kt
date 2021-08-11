package com.musicorumapp.mobile.ui.pages.artist

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.google.accompanist.placeholder.material.placeholder
import com.musicorumapp.mobile.R
import com.musicorumapp.mobile.api.models.Artist
import com.musicorumapp.mobile.api.models.LastfmEntity
import com.musicorumapp.mobile.api.models.MusicorumResource
import com.musicorumapp.mobile.states.LocalNavigationContext
import com.musicorumapp.mobile.ui.theme.MusicorumTheme
import com.musicorumapp.mobile.ui.theme.PaddingSpacing


@Composable
fun SimilarArtists(
    artists: List<Artist>? = null
) {

    val navigationContext = LocalNavigationContext.current
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.width(PaddingSpacing.HorizontalMainPadding))

            if (artists != null) {
                artists.forEach {
                    SimilarArtistItem(
                        it,
                        isLast = artists.last() === it,
                        modifier = Modifier.clickable {
                            val id = navigationContext.addArtist(it)
                            navigationContext.navigationController?.navigate("artist/$id")
                        }
                    )
                }
            } else {
                for (x in 1..4) {
                    SimilarArtistItem(isLast = (x == 4))
                }
            }

            Spacer(modifier = Modifier.width(PaddingSpacing.HorizontalMainPadding))
        }
    }
}

@Composable
fun SimilarArtistItem(
    artist: Artist? = null,
    isLast: Boolean = false,
    modifier: Modifier = Modifier
) {
    val size = 140.dp
    val artistPlaceholderPainter = painterResource(id = LastfmEntity.ARTIST.asDrawableSource())
    val padding = if (isLast) 0 else 12

    val imageURL = remember {
        mutableStateOf<String?>(artist?.imageURL)
    }

    println("---------------------- IMAGE URL $imageURL")
    
    LaunchedEffect(artist) {
        artist?.onResourcesChange {
            imageURL.value = artist.imageURL
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(end = padding.dp)
            .composed { modifier }
            .clip(RoundedCornerShape(2.dp))
    ) {
        Image(
            painter = if (imageURL.value != null) rememberImagePainter(
                imageURL.value,
                builder = {
                    crossfade(true)
                    placeholder(LastfmEntity.ARTIST.asDrawableSource())
                }
            )
            else artistPlaceholderPainter,
            contentDescription = artist?.name,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .clip(RoundedCornerShape(size))
                .size(size)
        )

        Text(
            artist?.name.orEmpty(),
            modifier = Modifier
                .placeholder(
                    visible = artist?.name == null
                )
                .width(size - 8.dp),
            maxLines = 1,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            fontSize = 13.sp
        )
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 400)
@Composable
fun SimilarArtistsPreview() {
    val sampleArtist = Artist(
        name = "Mussum Ipsum, cacilds vidis litro abertis. Cevadis im ampola pa arma uma pindureta.",
        listeners = 20,
        url = ""
    )
    MusicorumTheme {
        Scaffold {
            Column(
                modifier = Modifier.padding(5.dp)
            ) {
                SimilarArtists()
                Spacer(modifier = Modifier.height(6.dp))
                SimilarArtists(
                    listOf(
                        sampleArtist,
                        Artist.fromSample(),
                        Artist.fromSample()
                    )
                )
            }
        }
    }
}
