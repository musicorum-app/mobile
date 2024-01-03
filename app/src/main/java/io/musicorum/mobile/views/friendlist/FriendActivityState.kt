package io.musicorum.mobile.views.friendlist

import io.musicorum.mobile.serialization.entities.Track

data class FriendActivityState(
    val track: Track? = null,
    val loading: Boolean = false,
    val nowPlaying: Boolean = false
)