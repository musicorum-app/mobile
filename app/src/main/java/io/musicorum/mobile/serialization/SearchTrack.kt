package io.musicorum.mobile.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchTrack(
    val name: String,
    val artist: String,
    @SerialName("image")
    val images: List<Image>
)
