package io.musicorum.mobile.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    @SerialName("tag")
    val tags: List<TagData>
)

@Serializable
data class TagData(
    val name: String
)
