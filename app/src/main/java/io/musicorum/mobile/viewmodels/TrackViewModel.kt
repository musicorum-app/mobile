package io.musicorum.mobile.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.ktor.endpoints.TrackEndpoint
import io.musicorum.mobile.serialization.Track
import kotlinx.coroutines.launch

class TrackViewModel : ViewModel() {
    val track: MutableLiveData<Track> by lazy { MutableLiveData<Track>() }

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
}