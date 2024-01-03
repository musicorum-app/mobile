package io.musicorum.mobile.views.individual.user

import io.musicorum.mobile.serialization.TopAlbum
import io.musicorum.mobile.serialization.User
import io.musicorum.mobile.serialization.entities.TopArtist
import io.musicorum.mobile.serialization.entities.Track

data class UserState(
    val user: User? = null,
    val topArtists: List<TopArtist>? = null,
    val recentTracks: List<Track>? = null,
    val topAlbums: List<TopAlbum>? = null,
    val isRefreshing: Boolean = false,
    val hasError: Boolean = false,
    val isPinned: Boolean = false,
    val showPin: Boolean = true,
    val canPin: Boolean = true,
)
