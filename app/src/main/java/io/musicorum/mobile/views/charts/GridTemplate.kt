package io.musicorum.mobile.views.charts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.musicorum.mobile.coil.PlaceholderType
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.serialization.TopAlbum
import io.musicorum.mobile.serialization.entities.TopArtist
import io.musicorum.mobile.serialization.entities.Track

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun GridTemplate(entityList: List<Any>) {
    val entityWidth = 147.dp
    FlowRow(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
    ) {
        entityList.forEach { any ->
            when (any) {
                is TopArtist -> {
                    val model = defaultImageRequestBuilder(
                        url = any.bestImageUrl,
                        placeholderType = PlaceholderType.ARTIST
                    )
                    Column(modifier = Modifier.padding(10.dp)) {
                        AsyncImage(
                            model,
                            null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .size(entityWidth)
                                .clip(CircleShape)
                        )
                        Text(
                            text = any.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.width(entityWidth)
                        )
                        Text("${any.playCount} scrobbles")
                    }
                }

                is TopAlbum -> {
                    val model = defaultImageRequestBuilder(
                        url = any.bestImageUrl,
                        placeholderType = PlaceholderType.ALBUM
                    )
                    Column(modifier = Modifier.padding(vertical = 10.dp)) {
                        AsyncImage(
                            model,
                            null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .size(entityWidth)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Text(
                            text = any.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.width(entityWidth)
                        )
                        Text("${any.playCount} plays")
                    }
                }

                is Track -> {
                    val model = defaultImageRequestBuilder(
                        url = any.bestImageUrl,
                        placeholderType = PlaceholderType.TRACK
                    )
                    Column(modifier = Modifier.padding(vertical = 10.dp)) {
                        AsyncImage(
                            model,
                            null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .size(entityWidth)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Text(
                            text = any.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.width(entityWidth)
                        )
                        Text("${any.playCount} plays")
                    }
                }
            }
        }
    }
}