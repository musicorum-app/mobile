package io.musicorum.mobile.serialization

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class TopTracks(
    @SerialName("toptracks")
    val topTracks: TopTracksData
)

@kotlinx.serialization.Serializable
data class TopTracksData(
    @SerialName("track")
    val tracks: List<Track>
)