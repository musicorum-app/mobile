package io.musicorum.mobile.serialization

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Track(
    val artist: Artist,
    val image: List<Image>? = null,
    val name: String,
    @SerialName("@attr")
    val attributes: TrackAttributes? = null,
    val url: String,
    @SerialName("toptags")
    val topTags: Tag? = null,
    val album: Album? = null,
    @SerialName("userplaycount")
    private val _userPlayCount: String? = null,
    val date: TrackDate? = null,
    @SerialName("playcount")
    private val _playCount: String? = null,
    private val _listeners: String? = null
) {
    val userPlayCount = _userPlayCount?.toLongOrNull()
    val playCount = _playCount?.toLongOrNull()
    val listeners = _listeners?.toLongOrNull()
}

@kotlinx.serialization.Serializable
data class TrackAttributes(
    @SerialName("nowplaying")
    val nowPlaying: String? = null
)

@kotlinx.serialization.Serializable
data class TrackDate(
    val uts: String
)
