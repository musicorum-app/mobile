package io.musicorum.mobile.serialization.musicorum

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class TrackResponse(
    val resources: List<Resources>? = null,
    val album: String? = null,
    @SerialName("preferred_resource")
    val preferredResource: String? = null
) {
    val bestResource = resources?.find { it.hash == preferredResource }
}
