package io.musicorum.mobile.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.ktor.endpoints.AlbumEndpoint
import io.musicorum.mobile.ktor.endpoints.InnerAlbum
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumArtistEndpoint
import io.musicorum.mobile.serialization.Artist
import kotlinx.coroutines.launch

class AlbumViewModel : ViewModel() {
    val album: MutableLiveData<InnerAlbum> by lazy { MutableLiveData<InnerAlbum>() }
    val artistImage: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    fun getAlbum(albumName: String, artistName: String) {
        viewModelScope.launch {
            val res = AlbumEndpoint().getInfo(albumName, artistName)
            res?.let {
                album.value = it
            }
        }
        viewModelScope.launch {
            val musRes = MusicorumArtistEndpoint().fetchArtist(listOf(Artist(artistName)))
            val artistBestImage = musRes.getOrNull(0)?.resources?.getOrNull(0)?.bestImageUrl
            artistBestImage?.let {
                artistImage.value = it
            }
        }
    }
}