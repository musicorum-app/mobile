package io.musicorum.mobile.utils

import androidx.compose.runtime.mutableStateListOf
import io.musicorum.mobile.serialization.Track

class RecentScrobblesManager {
    val recentScrobbles = mutableStateListOf<Track>()
}