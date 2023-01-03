package io.musicorum.mobile.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.ktor.endpoints.FetchPeriod
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumTrackEndpoint
import io.musicorum.mobile.serialization.TopTracks
import kotlinx.coroutines.launch

class MostListenedViewModel : ViewModel() {
    val mosListenedTracks by lazy { MutableLiveData<TopTracks>() }
    val error by lazy { MutableLiveData<Boolean>(null) }

    suspend fun fetchMostListened(username: String, period: FetchPeriod?, limit: Int?) {
        viewModelScope.launch {
            val res = UserEndpoint.getTopTracks(username, period, limit)
            if (res == null) {
                error.value = true
                return@launch
            }
            val musicorumTrRes = MusicorumTrackEndpoint.fetchTracks(res.topTracks.tracks)
            musicorumTrRes?.forEachIndexed { i, tr ->
                val url = tr.resources?.getOrNull(0)?.bestImageUrl
                res.topTracks.tracks[i].bestImageUrl = url ?: ""
            }
            mosListenedTracks.value = res
        }
    }
}