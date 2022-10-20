package io.musicorum.mobile.serialization

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Artist(
    @SerialName("#text")
    private val nameText: String? = null,
    private val _name: String? = null,
) {
    val name = nameText ?: _name ?: "Unknown"
}
