package io.musicorum.mobile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumTrackEndpoint
import io.musicorum.mobile.models.FetchPeriod
import io.musicorum.mobile.repositories.LocalUserRepository
import io.musicorum.mobile.serialization.entities.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MostListenedViewModel(application: Application) : AndroidViewModel(application) {
    val mosListenedTracks = MutableLiveData<List<Track>>(emptyList())
    val error = MutableLiveData<Boolean>(null)
    lateinit var job: Job
    val ctx = application

    fun fetchMostListened(period: FetchPeriod?, limit: Int?) {
        job = viewModelScope.launch {
            val localUser = LocalUserRepository(ctx).getUser()
            val res = UserEndpoint.getTopTracks(localUser.username, period, limit)
            if (res == null) {
                error.value = true
                return@launch
            }
            val musicorumTrRes = MusicorumTrackEndpoint.fetchTracks(res.topTracks.tracks)
            musicorumTrRes.forEachIndexed { i, tr ->
                val url = tr?.resources?.getOrNull(0)?.bestImageUrl
                res.topTracks.tracks[i].bestImageUrl = url ?: ""
            }
            mosListenedTracks.value = res.topTracks.tracks
        }
    }

    init {
        fetchMostListened(FetchPeriod.WEEK, null)
    }
}