package io.musicorum.mobile.repositories

import androidx.compose.runtime.mutableStateOf
import io.musicorum.mobile.serialization.RecentTracks

class ScrobbleRepository {
    val recentScrobbles = mutableStateOf<RecentTracks?>(null)

    fun updateData(newData: RecentTracks) {
        recentScrobbles.value = newData
    }
}