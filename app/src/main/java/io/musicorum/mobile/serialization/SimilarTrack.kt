package io.musicorum.mobile.serialization

import io.musicorum.mobile.serialization.entities.Track
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SimilarTrack(
    @SerialName("similartracks")
    val similarTracks: SimilarTrackData
)

@Serializable
data class SimilarTrackData(
    @SerialName("track")
    val tracks: List<Track>
)
