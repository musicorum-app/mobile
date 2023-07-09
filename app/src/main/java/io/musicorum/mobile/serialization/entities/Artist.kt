package io.musicorum.mobile.serialization.entities

import io.musicorum.mobile.serialization.Image
import io.musicorum.mobile.serialization.Tag
import io.musicorum.mobile.serialization.Wiki
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class Artist @OptIn(ExperimentalSerializationApi::class) constructor(
    @JsonNames("#text")
    val name: String = "Unknown",
    @JsonNames("image")
    var images: List<Image>? = null,
    val similar: InnerArtist? = null,
    val stats: ListenStats? = null,
    val tags: Tag? = null,
    val bio: Wiki? = null
) {
    var bestImageUrl = images?.find { it.size == "extralarge" }?.url
        ?: images?.find { it.size == "large" }?.url
        ?: images?.find { it.size == "medium" }?.url
        ?: images?.find { it.size == "small" }?.url
        ?: images?.find { it.size == "unknown" }?.url
        ?: ""

    // TODO: generic data class extension? (T.findBestImage())
}

@Serializable
data class ListenStats(
    @SerialName("playcount")
    val playCount: Long,
    val listeners: Long,
    @SerialName("userplaycount")
    val userPlayCount: Long
)

@Serializable
data class InnerArtist(
    val artist: List<Artist>
)
