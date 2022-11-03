package io.musicorum.mobile.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TopAlbumsResponse(
    @SerialName("topalbums")
    val topAlbums: InnerTopAlbumsResponse
)

@Serializable
data class InnerTopAlbumsResponse(
    @SerialName("album")
    val albums: List<TopAlbum>
)

@Serializable
data class TopAlbum(
    private val _name: String? = null,
    private val title: String? = null,
    @SerialName("#text")
    private val text: String? = null,
    @SerialName("image")
    val images: List<Image>? = null,
    val artist: Artist? = null,
    @SerialName("playcount")
    val playCount: String? = null
) {
    val name = _name ?: title ?: text ?: "Unknown"
    var bestImageUrl = images?.find { it.size == "extralarge" }?.url
        ?: images?.find { it.size == "large" }?.url
        ?: images?.find { it.size == "medium" }?.url
        ?: images?.find { it.size == "small" }?.url
        ?: images?.find { it.size == "unknown" }?.url
        ?: ""

}
