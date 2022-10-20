package io.musicorum.mobile.serialization

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Album(
    val name: String? = null,
    @SerialName("image")
    val images: List<Image>? = null
)
