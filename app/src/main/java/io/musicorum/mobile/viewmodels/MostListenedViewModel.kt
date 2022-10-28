package io.musicorum.mobile.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.ktor.endpoints.FetchPeriod
import io.musicorum.mobile.ktor.endpoints.TopTracksEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumTrackEndpoint
import io.musicorum.mobile.serialization.TopTracks
import kotlinx.coroutines.launch

class MostListenedViewModel : ViewModel() {
    val mosListenedTracks: MutableLiveData<TopTracks> by lazy { MutableLiveData<TopTracks>() }

    suspend fun fetchMostListened(username: String, period: FetchPeriod?, limit: Int?) {
        viewModelScope.launch {
            val res = TopTracksEndpoint().fetchTopTracks(username, period, limit)
            val musicorumTrRes = MusicorumTrackEndpoint().fetchTracks(res.topTracks.tracks)
            musicorumTrRes.forEachIndexed { i, tr ->
                val url = tr.resources?.getOrNull(0)?.bestImageUrl
                res.topTracks.tracks[i].images?.onEach { img -> img.url = url ?: return@onEach }
            }
            mosListenedTracks.value = res
        }
    }
}