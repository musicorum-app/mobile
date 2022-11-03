package io.musicorum.mobile.serialization

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class TopArtist(
    val name: String,
    @SerialName("playcount")
    private val _playCount: String,
    @SerialName("image")
    val images: List<Image>? = null
) {
    val playCount = _playCount.toIntOrNull() ?: 0
    var bestImageUrl = images?.find { it.size == "extralarge" }?.url
        ?: images?.find { it.size == "large" }?.url
        ?: images?.find { it.size == "medium" }?.url
        ?: images?.find { it.size == "small" }?.url
        ?: images?.find { it.size == "unknown" }?.url
        ?: ""
}
