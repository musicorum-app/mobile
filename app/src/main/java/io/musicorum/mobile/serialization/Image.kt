package io.musicorum.mobile.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Image(
    val size: String,
    @SerialName("#text")
    var url: String
)