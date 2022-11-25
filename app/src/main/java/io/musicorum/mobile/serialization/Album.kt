package io.musicorum.mobile.serialization

import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumAlbumEndpoint
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull

@Serializable
data class Album @OptIn(ExperimentalSerializationApi::class) constructor(
    @JsonNames(*arrayOf("title", "#text"))
    var name: String = "Unknown",
    @SerialName("image")
    val images: List<Image>? = null,
    val artist: String? = null,
    @SerialName("playcount")
    val playCount: String? = null,
    val tags: Tag? = null,
    val tracks: AlbumTrack? = null,
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

    suspend fun fetchExternalImage(): String {
        val musRes = MusicorumAlbumEndpoint().fetchAlbums(listOf(this))
        return musRes?.getOrNull(0)?.resources?.getOrNull(0)?.bestImageUrl ?: ""
        // TODO fallback to a placeholder image
    }
}

@Serializable
data class AlbumTrack(
    @SerialName("track")
    val tracks: List<Track>
)
