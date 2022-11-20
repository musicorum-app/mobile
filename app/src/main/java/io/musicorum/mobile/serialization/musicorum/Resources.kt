package io.musicorum.mobile.serialization.musicorum

@kotlinx.serialization.Serializable
data class Resources(
    val images: List<Images>,
    val hash: String
) {
    val bestImageUrl = images.find { it.size == "EXTRA_LARGE" }?.url
        ?: images.find { it.size == "LARGE" }?.url
        ?: images.find { it.size == "MEDIUM" }?.url
        ?: images.find { it.size == "SMALL" }?.url
        ?: images.find { it.size == "EXTRA_SMALL" }?.url
        ?: ""
}

@kotlinx.serialization.Serializable
data class Images(
    val url: String,
    val size: String
)