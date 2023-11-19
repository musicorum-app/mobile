package io.musicorum.mobile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.ktor.endpoints.AlbumEndpoint
import io.musicorum.mobile.ktor.endpoints.InnerAlbum
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumArtistEndpoint
import io.musicorum.mobile.repositories.LocalUserRepository
import io.musicorum.mobile.serialization.entities.Artist
import kotlinx.coroutines.launch

class AlbumViewModel(application: Application) : AndroidViewModel(application) {
    val album by lazy { MutableLiveData<InnerAlbum>() }
    val artistImage by lazy { MutableLiveData<String>() }
    val errored by lazy { MutableLiveData(false) }
    val ctx = application

    fun getAlbum(albumName: String, artistName: String) {
        viewModelScope.launch {
            val user = LocalUserRepository(ctx).getUser()
            val res = AlbumEndpoint.getInfo(albumName, artistName, user.username)
            album.value = res
            if (res == null) errored.value = true
        }
        viewModelScope.launch {
            val musRes = MusicorumArtistEndpoint.fetchArtist(listOf(Artist(artistName)))
            val artistBestImage = musRes.getOrNull(0)?.resources?.getOrNull(0)?.bestImageUrl
            artistBestImage?.let {
                artistImage.value = it
            }
        }
    }
}