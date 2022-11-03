package io.musicorum.mobile.serialization

import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumAlbumEndpoint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Album(
    private val name: String? = null,
    private val title: String? = null,
    @SerialName("#text")
    private val text: String? = null,
    @SerialName("image")
    val images: List<Image>? = null,
    val artist: String? = null,
    @SerialName("playcount")
    val playCount: String? = null,
    val tags: Tag? = null,
    val tracks: AlbumTrack? = null,
    val listeners: String? = null,
    @SerialName("userplaycount")
    val userPlayCount: String? = null,
) {
    val albumName = name ?: title ?: text ?: "Unknown"
    var bestImageUrl = images?.find { it.size == "extralarge" }?.url
        ?: images?.find { it.size == "large" }?.url
        ?: images?.find { it.size == "medium" }?.url
        ?: images?.find { it.size == "small" }?.url
        ?: images?.find { it.size == "unknown" }?.url
        ?: ""

    suspend fun fetchExternalImage(): String {
        val musRes = MusicorumAlbumEndpoint().fetchAlbums(listOf(this))
        return musRes[0].resources?.getOrNull(0)?.bestImageUrl ?: ""
        // TODO fallback to a placeholder images URL
    }
}

@Serializable
data class AlbumTrack(
    @SerialName("track")
    val tracks: List<Track>
)
