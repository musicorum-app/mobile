package io.musicorum.mobile.serialization

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class RecentTracks(
    @SerialName("recenttracks")
    val recentTracks: RecentTracksData
)

@kotlinx.serialization.Serializable
data class RecentTracksData(
    @SerialName("track")
    val tracks: List<Track>,
    @SerialName("@attr")
    val recentTracksAttributes: RecentTracksAttributes
)

@kotlinx.serialization.Serializable
data class RecentTracksAttributes(
    val total: String
)