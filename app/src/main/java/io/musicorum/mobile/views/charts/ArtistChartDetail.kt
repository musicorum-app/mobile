package io.musicorum.mobile.views.charts

import android.icu.text.CompactDecimalFormat
import android.icu.util.ULocale
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.musicorum.mobile.coil.PlaceholderType
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.serialization.entities.TopArtist

@Composable
internal fun ArtistChartDetail(artists: List<TopArtist>?, viewMode: ViewMode) {
    val scrollState = rememberScrollState()
    if (viewMode == ViewMode.List) {
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            artists?.forEach {
                val painter = defaultImageRequestBuilder(
                    url = it.bestImageUrl,
                    placeholderType = PlaceholderType.ARTIST
                )
                val scrobbleCount = CompactDecimalFormat.getInstance(
                    ULocale.forLocale(java.util.Locale.getDefault()),
                    CompactDecimalFormat.CompactStyle.SHORT
                ).format(it.playCount)

                ListItem(
                    headlineContent = { Text(it.name) },
                    supportingContent = { Text(text = "$scrobbleCount scrobbles") },
                    leadingContent = {
                        AsyncImage(
                            model = painter,
                            contentDescription = null,
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            contentScale = ContentScale.FillBounds
                        )
                    }
                )
            }
        }
    } else {
        if (artists != null) {
            GridTemplate(entityList = artists)
        }
    }
}