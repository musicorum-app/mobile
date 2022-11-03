package io.musicorum.mobile.serialization

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class TopArtistsResponse(
    @SerialName("topartists")
    val topArtists: InnerTopArtistsResponse
)

@kotlinx.serialization.Serializable
data class InnerTopArtistsResponse(
    @SerialName("artist")
    val artists: List<TopArtist>
)
