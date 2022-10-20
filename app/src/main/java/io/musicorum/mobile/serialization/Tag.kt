package io.musicorum.mobile.serialization

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Tag(
    @SerialName("tag")
    val tags: List<TagData>
)

@kotlinx.serialization.Serializable
data class TagData(
    val name: String
)
