package io.musicorum.mobile.serialization.musicorum

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class TrackResponse(
    val resources: List<Resources> = emptyList(),
    val album: String = "Unknown",
    @SerialName("preferred_resource")
    val preferredResource: String? = ""
) {
    val bestResource = resources.find { it.hash == preferredResource }
}
