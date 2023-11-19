package io.musicorum.mobile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.ktor.client.plugins.ServerResponseException
import io.musicorum.mobile.ktor.endpoints.TrackEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumAlbumEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumArtistEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumTrackEndpoint
import io.musicorum.mobile.repositories.LocalUserRepository
import io.musicorum.mobile.serialization.Image
import io.musicorum.mobile.serialization.SimilarTrack
import io.musicorum.mobile.serialization.entities.Album
import io.musicorum.mobile.serialization.entities.Artist
import io.musicorum.mobile.serialization.entities.Track
import kotlinx.coroutines.launch

class TrackViewModel(application: Application) : AndroidViewModel(application) {
    val track by lazy { MutableLiveData<Track>() }
    val similar by lazy { MutableLiveData<SimilarTrack>() }
    val artistCover by lazy { MutableLiveData<String>() }
    val error by lazy { MutableLiveData<Boolean>(null) }
    val ctx = application

    suspend fun fetchTrack(
        trackName: String,
        artist: String,
        autoCorrect: Boolean?
    ) {
        viewModelScope.launch {
            val user = LocalUserRepository(ctx).getUser()
            val res = TrackEndpoint.getTrack(trackName, artist, user.username, autoCorrect)
            val musRes = MusicorumTrackEndpoint.fetchTracks(listOf(res!!.track))
            musRes.getOrNull(0)?.bestResource?.bestImageUrl?.let {
                res.track.album =
                    Album(
                        name = musRes.getOrNull(0)?.album ?: res.track.name,
                        images = listOf(Image("unknown", it)),
                        tags = null,
                        _tracks = null,
                        artist = res.track.artist.name
                    )
            }


            if (res.track.artist.images.isNullOrEmpty()) {
                val musArtistRes =
                    MusicorumArtistEndpoint.fetchArtist(listOf(res.track.artist))
                musArtistRes[0].bestResource?.bestImageUrl?.let {
                    res.track.artist.bestImageUrl = it
                }
            }

            val musicorumReqAlbum =
                musRes.getOrNull(0)?.album?.let { Album(name = it, artist = artist) }

            try {
                val musARes = MusicorumAlbumEndpoint.fetchAlbums(listOf(musicorumReqAlbum))
                res.track.album?.bestImageUrl =
                    musARes[0]?.resources?.getOrNull(0)?.bestImageUrl.toString()
            } catch (_: ServerResponseException) {
            }

            track.value = res.track
        }
    }

    suspend fun fetchSimilar(baseTrack: Track, limit: Int?, autoCorrect: Boolean?) {
        viewModelScope.launch {
            val res = TrackEndpoint.fetchSimilar(baseTrack, limit, autoCorrect)
            if (res == null) {
                error.value = true
                return@launch
            }
            if (res.similarTracks.tracks.isNotEmpty()) {
                val resourceRes = MusicorumTrackEndpoint.fetchTracks(res.similarTracks.tracks)
                resourceRes.forEachIndexed { index, trackResponse ->
                    val imageUrl = trackResponse?.resources?.getOrNull(0)?.bestImageUrl
                    res.similarTracks.tracks[index].bestImageUrl = imageUrl ?: ""
                }
                similar.value = res
            }
        }
    }

    suspend fun fetchArtistCover(artist: Artist) {
        val list = listOf(artist)
        val res = MusicorumArtistEndpoint.fetchArtist(list)
        artistCover.value = res[0].resources.getOrNull(0)?.bestImageUrl
    }

    fun updateFavoritePreference(track: Track) {
        viewModelScope.launch {
            TrackEndpoint.updateFavoritePreference(track, ctx)
        }
    }

}