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
    val scrobbles: String,
    @SerialName("artist_count")
    val artistCount: String? = null,
    @SerialName("album_count")
    val albumCount: String? = null,
    val registered: Registered
) {
    var bestImageUrl = images.find { it.size == "extralarge" }?.url
        ?: images.find { it.size == "large" }?.url
        ?: images.find { it.size == "medium" }?.url
        ?: images.find { it.size == "small" }?.url
        ?: images.find { it.size == "unknown" }?.url
        ?: ""

    @SerialName("realname")
    val realName: String? = null
        get() {
            return if (field?.isEmpty() == true) {
                null
            } else {
                field
            }
        }
}

