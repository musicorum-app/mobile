package io.musicorum.mobile.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.ktor.endpoints.TrackEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumAlbumEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumArtistEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumTrackEndpoint
import io.musicorum.mobile.serialization.*
import kotlinx.coroutines.launch

class TrackViewModel : ViewModel() {
    val track by lazy { MutableLiveData<Track>() }
    val similar by lazy { MutableLiveData<SimilarTrack>() }
    val artistCover by lazy { MutableLiveData<String>() }
    val error by lazy { MutableLiveData<Boolean>(null) }

    suspend fun fetchTrack(
        trackName: String,
        artist: String,
        username: String?,
        autoCorrect: Boolean?
    ) {
        viewModelScope.launch {
            val res = TrackEndpoint().getTrack(trackName, artist, username, autoCorrect)

            val musRes = MusicorumTrackEndpoint().fetchTracks(listOf(res!!.track))
            musRes?.getOrNull(0)?.bestResource?.bestImageUrl?.let {
                res.track.album =
                    Album(
                        name = musRes.getOrNull(0)?.album ?: res.track.name,
                        images = listOf(Image("unknown", it)),
                        tags = null,
                        tracks = null,
                        artist = res.track.artist.name
                    )
            }


            if (res.track.artist.images.isNullOrEmpty()) {
                val musArtistRes =
                    MusicorumArtistEndpoint().fetchArtist(listOf(res.track.artist))
                musArtistRes[0].bestResource?.bestImageUrl?.let {
                    res.track.artist.bestImageUrl = it
                }
            }

            val musicorumReqAlbum =
                musRes?.getOrNull(0)?.album?.let { Album(name = it, artist = artist) }

            val musARes = MusicorumAlbumEndpoint().fetchAlbums(listOf(musicorumReqAlbum))
            res.track.album?.bestImageUrl =
                musARes?.get(0)?.resources?.getOrNull(0)?.bestImageUrl.toString()

            track.value = res.track
        }
    }

    suspend fun fetchSimilar(baseTrack: Track, limit: Int?, autoCorrect: Boolean?) {
        viewModelScope.launch {
            val res = TrackEndpoint().fetchSimilar(baseTrack, limit, autoCorrect)
            if (res == null) {
                error.value = true
                return@launch
            }
            if (res.similarTracks.tracks.isNotEmpty()) {
                val resourceRes = MusicorumTrackEndpoint().fetchTracks(res.similarTracks.tracks)
                resourceRes?.forEachIndexed { index, trackResponse ->
                    val imageUrl = trackResponse.resources?.getOrNull(0)?.bestImageUrl
                    res.similarTracks.tracks[index].bestImageUrl = imageUrl ?: ""
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

    fun updateFavoritePreference(track: Track, ctx: Context) {
        viewModelScope.launch {
            TrackEndpoint().updateFavoritePreference(track, ctx)
        }
    }

}