package io.musicorum.mobile.coil

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import coil.request.CachePolicy
import coil.request.ImageRequest
import io.musicorum.mobile.utils.Placeholders

enum class PlaceholderType {
    ARTIST, TRACK, USER, ALBUM
}


@Composable
fun defaultImageRequestBuilder(
    url: String?,
    placeholderType: PlaceholderType = PlaceholderType.TRACK
): ImageRequest {
    val builder = ImageRequest.Builder(LocalContext.current)
        .diskCacheKey(url)
        .diskCachePolicy(CachePolicy.ENABLED)
        .data(url)
        .crossfade(true)
        .networkCachePolicy(CachePolicy.READ_ONLY)


    when (placeholderType) {
        PlaceholderType.ARTIST -> {
            builder
                .placeholder(Placeholders.ARTIST.asDrawable())
                .error(Placeholders.ARTIST.asDrawable())
                .fallback(Placeholders.ARTIST.asDrawable())
        }
        PlaceholderType.TRACK -> {
            builder
                .placeholder(Placeholders.TRACK.asDrawable())
                .error(Placeholders.TRACK.asDrawable())
                .fallback(Placeholders.TRACK.asDrawable())
        }
        PlaceholderType.ALBUM -> {
            builder
                .placeholder(Placeholders.ALBUM.asDrawable())
                .error(Placeholders.ALBUM.asDrawable())
                .fallback(Placeholders.ALBUM.asDrawable())
        }
        PlaceholderType.USER -> {
            builder
                .placeholder(Placeholders.USER.asDrawable())
                .error(Placeholders.USER.asDrawable())
                .fallback(Placeholders.USER.asDrawable())
        }
    }

    return builder.build()
}