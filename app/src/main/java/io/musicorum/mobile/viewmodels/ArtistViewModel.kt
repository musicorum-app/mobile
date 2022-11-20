package io.musicorum.mobile.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import io.musicorum.mobile.ktor.endpoints.ArtistEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumArtistEndpoint
import io.musicorum.mobile.serialization.Artist
import kotlinx.coroutines.launch

class ArtistViewModel : ViewModel() {
    val artist by lazy { MutableLiveData<Artist>() }
    val topAlbumImage by lazy { MutableLiveData<String>() }
    val palette by lazy { MutableLiveData<Palette>() }

    fun fetchArtist(artistName: String, username: String?) {
        viewModelScope.launch {
            val res = ArtistEndpoint().getInfo(artistName, username)
            if (res != null) {
                val artists = mutableListOf(res.artist)
                artists.addAll(res.artist.similar?.artist!!)

                val musArtistsRes =
                    MusicorumArtistEndpoint().fetchArtist(artists.toList())
                res.artist.bestImageUrl =
                    musArtistsRes.toMutableList().removeAt(0).bestResource?.bestImageUrl.toString()
                res.artist.similar.artist.onEachIndexed { i, artist ->
                    artist.bestImageUrl =
                        musArtistsRes.drop(1)
                            .getOrNull(i)?.resources?.getOrNull(0)?.bestImageUrl.toString()
                }
                artist.value = res.artist
            }

            ArtistEndpoint().getTopAlbums(artistName)?.topAlbums?.albums?.getOrNull(0)?.let {
                topAlbumImage.postValue(it.bestImageUrl)
            }
        }
    }
}