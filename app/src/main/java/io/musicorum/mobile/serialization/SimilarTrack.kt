package io.musicorum.mobile.serialization

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SimilarTrack(
    @SerialName("similartracks")
    val similarTracks: SimilarTrackData
)

@kotlinx.serialization.Serializable
data class SimilarTrackData(
    @SerialName("track")
    val tracks: List<Track>
)
