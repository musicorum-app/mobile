package io.musicorum.mobile.serialization

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Artist(
    @SerialName("#text")
    private val nameText: String? = null,
    private val _name: String? = null,
    var images: List<Image>? = null
) {
    val name = nameText ?: _name ?: "Unknown"
    val bestImageUrl = images?.find { it.size == "extralarge" }?.url
        ?: images?.find { it.size == "large" }?.url
        ?: images?.find { it.size == "medium" }?.url
        ?: images?.find { it.size == "small" }?.url
        ?: images?.find { it.size == "unknown" }?.url
        ?: ""
}
