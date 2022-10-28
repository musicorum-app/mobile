package io.musicorum.mobile.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.ktor.endpoints.RecentTracksEndpoint
import io.musicorum.mobile.serialization.RecentTracksData
import kotlinx.coroutines.launch

class RecentSrcobblesViewModel : ViewModel() {
    val recentTracks: MutableLiveData<RecentTracksData> by lazy { MutableLiveData<RecentTracksData>() }

    suspend fun fetchRecentTracks(
        username: String,
        from: String?,
        limit: Int?,
        extended: Boolean?
    ) {
        viewModelScope.launch {
            val res = RecentTracksEndpoint().getRecentTracks(username, from, limit, extended)
            recentTracks.value = res.recentTracks
        }
    }
}