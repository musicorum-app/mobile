package io.musicorum.mobile.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

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
data class TopAlbum @OptIn(ExperimentalSerializationApi::class) constructor(
    @JsonNames("title", "#text")
    val name: String = "Unknown",
    private val title: String? = null,
    @SerialName("#text")
    private val text: String? = null,
    @SerialName("image")
    val images: List<Image>? = null,
    val artist: Artist? = null,
    @SerialName("playcount")
    val playCount: String? = null
) {
    var bestImageUrl = images?.find { it.size == "extralarge" }?.url
        ?: images?.find { it.size == "large" }?.url
        ?: images?.find { it.size == "medium" }?.url
        ?: images?.find { it.size == "small" }?.url
        ?: images?.find { it.size == "unknown" }?.url
        ?: ""
}
