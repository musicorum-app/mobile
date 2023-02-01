package io.musicorum.mobile.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import coil.compose.AsyncImage
import io.ktor.http.encodeURLPathPart
import io.musicorum.mobile.LocalNavigation
import io.musicorum.mobile.R
import io.musicorum.mobile.coil.PlaceholderType
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.router.Routes
import io.musicorum.mobile.ui.theme.ContentSecondary
import io.musicorum.mobile.ui.theme.Typography
import io.musicorum.mobile.views.individual.PartialAlbum
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
) {
    val nav = LocalNavigation.current
    var appearsOnMod = Modifier
        .fillMaxWidth(0.5f)
        .padding(end = 5.dp)
        .clip(RoundedCornerShape(8.dp))

    Row(modifier = Modifier.padding(start = 20.dp)) {
        if (appearsOn != null) {
            val partialAlbum = PartialAlbum(
                appearsOn.first?.encodeURLPathPart() ?: "Unknown",
                from?.first ?: "Unknown"
            )
            val partialAlbumArgument = Json.encodeToString(partialAlbum)
            val interactionSource = MutableInteractionSource()
            if (appearsOn.first != null) {
                appearsOnMod = appearsOnMod.clickable(
                    interactionSource = interactionSource,
                    indication = LocalIndication.current
                ) {
                    nav?.navigate(Routes.album(partialAlbumArgument))
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(id = R.string.appears_on),
                    style = Typography.labelMedium,
                    color = ContentSecondary
                )
                Row(
                    modifier = appearsOnMod,
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
                    )
                    Text(
                        text = appearsOn.first ?: "Unknown",
                        style = Typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        if (from != null) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.from),
                    style = Typography.labelMedium,
                    color = ContentSecondary
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 5.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            nav?.navigate(Routes.artist(from.first ?: "unknown"))
                        },
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
                        style = Typography.titleLarge,
                        modifier = Modifier.padding(start = 10.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}