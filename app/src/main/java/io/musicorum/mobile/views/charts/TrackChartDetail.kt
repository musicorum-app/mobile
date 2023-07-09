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
import io.musicorum.mobile.serialization.entities.Track
import java.util.Locale

@Composable
internal fun TrackChartDetail(tracks: List<Track>?, viewMode: ViewMode) {
    val scrollState = rememberScrollState()
    if (viewMode == ViewMode.List) {
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            tracks?.forEach {
                val painter = defaultImageRequestBuilder(
                    url = it.bestImageUrl,
                    placeholderType = PlaceholderType.TRACK
                )
                val scrobbleCount = CompactDecimalFormat.getInstance(
                    ULocale.forLocale(Locale.getDefault()),
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
        if (tracks != null) {
            GridTemplate(entityList = tracks)
        }
    }
}