package io.musicorum.mobile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import io.musicorum.mobile.ktor.endpoints.ArtistEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumArtistEndpoint
import io.musicorum.mobile.repositories.LocalUserRepository
import io.musicorum.mobile.serialization.TopAlbum
import io.musicorum.mobile.serialization.entities.Artist
import io.musicorum.mobile.serialization.entities.Track
import kotlinx.coroutines.launch

class ArtistViewModel(application: Application) : AndroidViewModel(application) {
    val artist by lazy { MutableLiveData<Artist>() }
    val palette by lazy { MutableLiveData<Palette>() }
    val topTracks by lazy { MutableLiveData<List<Track>>() }
    val topAlbums by lazy { MutableLiveData<List<TopAlbum>>() }
    val ctx = application

    fun fetchArtist(artistName: String) {
        viewModelScope.launch {
            val user = LocalUserRepository(ctx).getUser()
            val res = ArtistEndpoint.getInfo(artistName, user.username)

            if (res != null) {
                val artists = mutableListOf(res.artist)
                artists.addAll(res.artist.similar?.artist!!)

                val musArtistsRes =
                    MusicorumArtistEndpoint.fetchArtist(artists.toList())
                res.artist.bestImageUrl =
                    musArtistsRes.toMutableList().removeAt(0).bestResource?.bestImageUrl.toString()
                res.artist.similar.artist.onEachIndexed { i, artist ->
                    artist.bestImageUrl =
                        musArtistsRes.drop(1)
                            .getOrNull(i)?.resources?.getOrNull(0)?.bestImageUrl.toString()
                }
                artist.value = res.artist
            }
        }
    }

    fun fetchTopAlbums(name: String) {
        viewModelScope.launch {
            val res = ArtistEndpoint.getTopAlbums(name)?.topAlbums
            topAlbums.value = res?.albums
        }
    }

    fun fetchTopTracks(name: String) {
        viewModelScope.launch {
            val res = ArtistEndpoint.getTopTracks(name)?.topTracks
            topTracks.value = res?.tracks
        }
    }
}