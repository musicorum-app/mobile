package io.musicorum.mobile.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import io.musicorum.mobile.R
import io.musicorum.mobile.coil.PlaceholderType
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.screens.individual.PartialAlbum
import io.musicorum.mobile.ui.theme.BodyLarge
import io.musicorum.mobile.ui.theme.BodySmall
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Generates a row using the provided appearsOn and from arguments
 * @param appearsOn A pair of the album name and album image URL
 * @param from A pair of the artist name and the artist image URL
 */
@Composable
fun ContextRow(
    appearsOn: Pair<String?, String?>?,
    from: Pair<String?, String?>?,
    nav: NavHostController?
) {
    Row(modifier = Modifier.padding(start = 20.dp)) {
        if (appearsOn != null) {
            val partialAlbum = PartialAlbum(appearsOn.first ?: "Unknown", from!!.first ?: "Unknown")
            val partialAlbumArgument = Json.encodeToString(partialAlbum)
            val interactionSource = MutableInteractionSource()
            Column {
                Text(text = stringResource(id = R.string.appears_on), style = BodySmall)
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { nav?.navigate("album/$partialAlbumArgument") },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = defaultImageRequestBuilder(
                            url = appearsOn.second,
                            placeholderType = PlaceholderType.ALBUM,
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .indication(interactionSource, LocalIndication.current)
                    )
                    Text(
                        text = appearsOn.first ?: "Unknown",
                        style = BodyLarge,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        if (from != null) {
            Column {
                Text(text = stringResource(R.string.from), style = BodySmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = defaultImageRequestBuilder(
                            url = from.second,
                            PlaceholderType.ALBUM
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = from.first ?: "Unknown",
                        style = BodyLarge,
                        modifier = Modifier.padding(start = 10.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}