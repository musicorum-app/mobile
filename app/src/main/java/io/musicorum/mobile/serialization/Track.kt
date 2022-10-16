package io.musicorum.mobile.serialization

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Track(
    val artist: Artist,
    val image: List<Image>,
    val name: String,
    @SerialName("@attr")
    val attributes: TrackAttributes? = null,
    val url: String,
    val date: TrackDate? = null,

    )
@kotlinx.serialization.Serializable
data class TrackAttributes(
    @SerialName("nowplaying")
    val nowPlaying: String? = null
)

@kotlinx.serialization.Serializable
data class TrackDate(
    val uts: String
)

