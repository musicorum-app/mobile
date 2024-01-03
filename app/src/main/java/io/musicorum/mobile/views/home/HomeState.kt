package io.musicorum.mobile.views.home

import androidx.palette.graphics.Palette
import io.musicorum.mobile.models.PartialUser
import io.musicorum.mobile.serialization.RecentTracks
import io.musicorum.mobile.serialization.UserData
import io.musicorum.mobile.serialization.entities.Track

data class HomeState(
    val user: PartialUser? = null,
    val userPalette: Palette? = null,
    val recentTracks: List<Track>? = null,
    val weekTracks: List<Track>? = null,
    val friends: List<UserData>? = null,
    val hasError: Boolean = false,
    val weeklyScrobbles: Int? = null,
    val isRefreshing: Boolean = false,
    val hasPendingScrobbles: Boolean = false,
    val showRewindCard: Boolean = false,
    val rewindCardMessage: String = "",
    val showSettingsBade: Boolean = false,
    val isOffline: Boolean = false,
    val friendsActivity: List<RecentTracks>? = null,
    val pinnedUsers: Set<String> = emptySet()
)
