package io.musicorum.mobile.serialization

@kotlinx.serialization.Serializable
data class NavigationTrack(
    val trackName: String,
    val trackArtist: String
)