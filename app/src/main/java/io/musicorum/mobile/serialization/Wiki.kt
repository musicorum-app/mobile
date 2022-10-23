package io.musicorum.mobile.serialization

@kotlinx.serialization.Serializable
data class Wiki(
    val published: String,
    val summary: String,
    val content: String
)
