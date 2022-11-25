package io.musicorum.mobile.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.ktor.endpoints.AlbumEndpoint
import io.musicorum.mobile.ktor.endpoints.InnerAlbum
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumArtistEndpoint
import io.musicorum.mobile.serialization.Artist
import io.musicorum.mobile.serialization.User
import kotlinx.coroutines.launch

class AlbumViewModel : ViewModel() {
    val album by lazy { MutableLiveData<InnerAlbum>() }
    val artistImage by lazy { MutableLiveData<String>() }
    val errored by lazy { MutableLiveData(false) }

    fun getAlbum(albumName: String, artistName: String, user: User?) {
        viewModelScope.launch {
            val res = AlbumEndpoint.getInfo(albumName, artistName, user?.user?.name)
            album.value = res
            if (res == null) errored.value = true
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