package io.musicorum.mobile.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecentTracks(
    @SerialName("recenttracks")
    val recentTracks: RecentTracksData
)

@Serializable
data class RecentTracksData(
    @SerialName("track")
    val tracks: List<Track>,
    @SerialName("@attr")
    val recentTracksAttributes: RecentTracksAttributes
)

@Serializable
data class RecentTracksAttributes(
    val total: String
)