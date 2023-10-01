package io.musicorum.mobile.serialization

@kotlinx.serialization.Serializable
data class Wiki(
    val published: String? = null,
    val summary: String,
    val content: String
)
