package io.musicorum.mobile.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val user: UserData
)


@Serializable
data class UserData(
    val name: String,
    @SerialName("image")
    val images: List<Image>,
    @SerialName("playcount")
    val scrobbles: String
) {
    var bestImageUrl = images.find { it.size == "extralarge" }?.url
        ?: images.find { it.size == "large" }?.url
        ?: images.find { it.size == "medium" }?.url
        ?: images.find { it.size == "small" }?.url
        ?: images.find { it.size == "unknown" }?.url
        ?: ""
}

