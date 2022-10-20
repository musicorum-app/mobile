package io.musicorum.mobile.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.ktor.endpoints.SimilarTracksEndpoint
import io.musicorum.mobile.ktor.endpoints.TrackEndpoint
import io.musicorum.mobile.serialization.SimilarTrack
import io.musicorum.mobile.serialization.Track
import kotlinx.coroutines.launch

class TrackViewModel : ViewModel() {
    val track: MutableLiveData<Track> by lazy { MutableLiveData<Track>() }
    val similar: MutableLiveData<SimilarTrack> by lazy { MutableLiveData<SimilarTrack>() }

    suspend fun fetchTrack(
        trackName: String,
        artist: String,
        username: String?,
        autoCorrect: Boolean?
    ) {
        viewModelScope.launch {
            val res = TrackEndpoint().getTrack(trackName, artist, username, autoCorrect)
            track.value = res.track
        }
    }

    suspend fun fetchSimilar(baseTrack: Track, limit: Int?, autoCorrect: Boolean?) {
        viewModelScope.launch {
            val res = SimilarTracksEndpoint().fetchSimilar(baseTrack, limit, autoCorrect)
            similar.value = res
        }
    }
}