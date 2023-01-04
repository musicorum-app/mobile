package io.musicorum.mobile.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*

private val json = Json { ignoreUnknownKeys = true }

@Serializable
data class Album @OptIn(ExperimentalSerializationApi::class) constructor(
    @JsonNames(*arrayOf("title", "#text"))
    var name: String = "Unknown",
    @SerialName("image")
    val images: List<Image>? = null,
    val artist: String? = null,
    @SerialName("playcount")
    val playCount: String? = null,
    private val tags: JsonElement? = null,
    @SerialName("tracks")
    val _tracks: JsonElement? = null,
    val listeners: String? = null,
    @SerialName("userplaycount")
    private val _userPlayCount: JsonPrimitive? = null,
    val wiki: Wiki? = null
) {
    val userPlayCount = _userPlayCount?.intOrNull
    var bestImageUrl = images?.find { it.size == "extralarge" }?.url
        ?: images?.find { it.size == "large" }?.url
        ?: images?.find { it.size == "medium" }?.url
        ?: images?.find { it.size == "small" }?.url
        ?: images?.find { it.size == "unknown" }?.url
        ?: ""

    val albumTags = try {
        if (tags?.jsonPrimitive?.content?.isEmpty() == true) {
            null
        } else {
            null
        }
    } catch (e: IllegalArgumentException) {
        json.decodeFromString<Tag>(tags?.jsonObject.toString())
    }

    @kotlinx.serialization.Transient
    val tracks = try {
        if (_tracks?.jsonArray != null) {
            val d = json.decodeFromString<AlbumTrack>(_tracks.jsonArray.toString())
            d.tracks
        } else null
    } catch (e: Exception) {
        if (_tracks?.jsonObject != null) {
            val d = json.decodeFromString<SingleAlbumTrack>(_tracks.jsonObject.toString())
            listOf(d.track)
        } else null
    }
}

@Serializable
data class AlbumTrack(
    @SerialName("track")
    val tracks: List<Track>
)

@Serializable
data class SingleAlbumTrack(
    val track: Track
)
