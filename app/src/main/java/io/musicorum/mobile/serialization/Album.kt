package io.musicorum.mobile.serialization

import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumAlbumEndpoint
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Album(
    private val _name: String? = null,
    private val title: String? = null,
    @SerialName("#text")
    private val text: String? = null,
    @SerialName("image")
    val images: List<Image>? = null,
    val artist: String? = null
) {
    val name = _name ?: title ?: text ?: "Unknown"
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
