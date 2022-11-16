package io.musicorum.mobile.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonNames

@kotlinx.serialization.Serializable
data class Artist @OptIn(ExperimentalSerializationApi::class) constructor(
    @JsonNames("#text")
    val name: String = "Unknown",
    var images: List<Image>? = null
) {
    var bestImageUrl = images?.find { it.size == "extralarge" }?.url
        ?: images?.find { it.size == "large" }?.url
        ?: images?.find { it.size == "medium" }?.url
        ?: images?.find { it.size == "small" }?.url
        ?: images?.find { it.size == "unknown" }?.url
        ?: ""

    // TODO: generic data class extension? (T.findBestImage())
}
