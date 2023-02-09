package io.musicorum.mobile.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNames
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.long

@Serializable
data class Track @OptIn(ExperimentalSerializationApi::class) constructor(
    var artist: Artist,
    @SerialName("image")
    var images: List<Image>? = null,
    val name: String,
    @SerialName("@attr")
    val attributes: TrackAttributes? = null,
    val url: String,
    val wiki: Wiki? = null,
    @SerialName("toptags")
    val topTags: Tag? = null,
    var album: Album? = null,
    @JsonNames(*arrayOf("userloved", "loved"))
    val _loved: String? = null,
    @SerialName("userplaycount")
    private val _userPlayCount: String? = null,
    val date: TrackDate? = null,
    @SerialName("playcount")
    private val _playCount: JsonElement? = null,
    private val _listeners: String? = null
) {
    val userPlayCount = _userPlayCount?.toLongOrNull()
    val playCount: Long? =
        (_playCount as? JsonPrimitive)?.long ?: (_playCount as? JsonPrimitive)?.content?.toLong()
    val listeners = _listeners?.toLongOrNull()
    var bestImageUrl = images?.find { it.size == "extralarge" }?.url
        ?: images?.find { it.size == "large" }?.url
        ?: images?.find { it.size == "medium" }?.url
        ?: images?.find { it.size == "small" }?.url
        ?: images?.find { it.size == "unknown" }?.url
        ?: ""

    @kotlinx.serialization.Transient
    val loved = _loved == "1" || _loved.toBoolean()
}

@Serializable
data class TrackAttributes(
    @SerialName("nowplaying")
    val nowPlaying: String? = null
)

@Serializable
data class TrackDate(
    val uts: String
)

