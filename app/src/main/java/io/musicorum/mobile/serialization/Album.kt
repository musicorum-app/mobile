package io.musicorum.mobile.serialization

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Album(
    private val _name: String? = null,
    private val title: String? = null,
    @SerialName("image")
    val images: List<Image>? = null
) {
    val name = _name ?: title ?: "Unknown"
    val bestImageUrl = images?.find { it.size == "extralarge" }?.url
        ?: images?.find { it.size == "large" }?.url
        ?: images?.find { it.size == "medium" }?.url
        ?: images?.find { it.size == "small" }?.url
        ?: images?.find { it.size == "unknown" }?.url
        ?: ""
}
