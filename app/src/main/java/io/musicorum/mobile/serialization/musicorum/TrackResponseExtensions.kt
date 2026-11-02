package io.musicorum.mobile.serialization.musicorum

internal fun TrackResponse.bestAvailableImageUrl(): String? {
    val preferredImage = bestResource?.bestImageUrl?.takeIf(String::isNotBlank)
    return preferredImage
        ?: resources.firstOrNull()?.bestImageUrl?.takeIf(String::isNotBlank)
}
