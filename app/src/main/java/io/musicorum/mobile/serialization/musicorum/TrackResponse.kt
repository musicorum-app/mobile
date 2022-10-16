package io.musicorum.mobile.serialization.musicorum

@kotlinx.serialization.Serializable
data class TrackResponse(
    val resources: List<Resources>,
)
