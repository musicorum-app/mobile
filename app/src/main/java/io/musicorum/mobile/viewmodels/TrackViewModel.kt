package io.musicorum.mobile.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.ktor.endpoints.SimilarTracksEndpoint
import io.musicorum.mobile.ktor.endpoints.TrackEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumArtistEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumTrackEndpoint
import io.musicorum.mobile.serialization.*
import kotlinx.coroutines.launch

class TrackViewModel : ViewModel() {
    val track: MutableLiveData<Track> by lazy { MutableLiveData<Track>() }
    val similar: MutableLiveData<SimilarTrack> by lazy { MutableLiveData<SimilarTrack>() }
    val artistCover: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    suspend fun fetchTrack(
        trackName: String,
        artist: String,
        username: String?,
        autoCorrect: Boolean?
    ) {
        viewModelScope.launch {
            val res = TrackEndpoint().getTrack(trackName, artist, username, autoCorrect)
            if (res.track.album == null) {
                val musRes = MusicorumTrackEndpoint().fetchTracks(listOf(res.track))
                val image =
                    musRes[0].resources?.getOrNull(0)?.bestImageUrl?.let { Image("unknown", it) }!!
                res.track.album = Album(res.track.name, listOf(image))
            }
            track.value = res.track
        }
    }

    suspend fun fetchSimilar(baseTrack: Track, limit: Int?, autoCorrect: Boolean?) {
        viewModelScope.launch {
            val res = SimilarTracksEndpoint().fetchSimilar(baseTrack, limit, autoCorrect)
            if (res.similarTracks.tracks.isNotEmpty()) {
                val resourceRes = MusicorumTrackEndpoint().fetchTracks(res.similarTracks.tracks)
                resourceRes.forEachIndexed { index, trackResponse ->
                    val imageUrl = trackResponse.resources?.get(0)?.bestImageUrl
                    res.similarTracks.tracks[index].image?.onEach { it ->
                        it.url = imageUrl ?: return@onEach
                    }
                }
                similar.value = res
            }
        }
    }

    suspend fun fetchArtistCover(artist: Artist) {
        val list = listOf(artist)
        val res = MusicorumArtistEndpoint().fetchArtist(list)
        artistCover.value = res[0].resources?.getOrNull(0)?.bestImageUrl
    }
}