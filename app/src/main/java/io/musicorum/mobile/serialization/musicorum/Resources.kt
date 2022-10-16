package io.musicorum.mobile.serialization.musicorum

private enum class ImageSize (_scale: Int) {
    EXTRA_SMALL(1),
    SMALL(2),
    MEDIUM(3),
    LARGE(4);

    val scale = _scale
}


@kotlinx.serialization.Serializable
data class Resources(
    val images: List<Images>
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