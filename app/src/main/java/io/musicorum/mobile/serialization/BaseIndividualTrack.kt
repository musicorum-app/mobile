package io.musicorum.mobile.serialization

import io.musicorum.mobile.serialization.entities.Track

@kotlinx.serialization.Serializable
data class BaseIndividualTrack(
    val track: Track
)