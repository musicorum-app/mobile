package io.musicorum.mobile.viewmodels

import android.app.Application
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumArtistEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumTrackEndpoint
import io.musicorum.mobile.models.FetchPeriod
import io.musicorum.mobile.repositories.LocalUserRepository
import io.musicorum.mobile.serialization.TopAlbum
import io.musicorum.mobile.serialization.entities.TopArtist
import io.musicorum.mobile.serialization.entities.TopTracksData
import io.musicorum.mobile.utils.createPalette
import io.musicorum.mobile.utils.getBitmap
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ChartsViewModel(application: Application) : AndroidViewModel(application) {
    val preferredColor: MutableLiveData<Color> = MutableLiveData()
    val topArtists: MutableLiveData<List<TopArtist>> = MutableLiveData()
    val topAlbums: MutableLiveData<List<TopAlbum>> = MutableLiveData()
    val topTracks: MutableLiveData<TopTracksData> = MutableLiveData()
    val period = MutableLiveData(FetchPeriod.WEEK)
    val busy = MutableLiveData(false)
    val _application = application

    init {
        viewModelScope.launch {
            val user = LocalUserRepository(application.applicationContext).partialUser.first()
            fetchAll(user.username)
        }
    }

    fun getColor(image: String, ctx: Context) {
        viewModelScope.launch {
            val bmp = getBitmap(image, ctx)
            val palette = createPalette(bmp)
            preferredColor.value = Color(palette.getVibrantColor(Color.Gray.toArgb()))
        }
    }

    suspend fun getTopArtists(user: String) {
        val res = UserEndpoint.getTopArtists(username = user, period = period.value, limit = 4)
        res?.let {
            val musRes = MusicorumArtistEndpoint.fetchArtist(res.topArtists.artists)
            it.topArtists.artists.onEachIndexed { i, artist ->
                artist.bestImageUrl = musRes?.getOrNull(i)?.bestResource?.bestImageUrl ?: ""
            }
            topArtists.value = it.topArtists.artists
        }
    }

    suspend fun getTopAlbums(user: String) {
        val res = UserEndpoint.getTopAlbums(user = user, period = period.value, limit = 4)
        res?.let {
            topAlbums.value = it.topAlbums.albums
        }
    }

    suspend fun getTopTracks(user: String) {
        val res = UserEndpoint.getTopTracks(user = user, period = period.value, limit = 4)
        res?.let {
            val musRes = MusicorumTrackEndpoint.fetchTracks(res.topTracks.tracks)
            res.topTracks.tracks.onEachIndexed { i, t ->
                t.bestImageUrl = musRes?.getOrNull(i)?.bestResource?.bestImageUrl ?: ""
            }
            topTracks.value = res.topTracks
        }
    }

    fun fetchAll(user: String) {
        busy.value = true
        viewModelScope.launch {
            getTopAlbums(user)
            getTopArtists(user)
            getTopTracks(user)
            busy.value = false
        }
    }

    fun updatePeriod(newPeriod: FetchPeriod) {
        viewModelScope.launch {
            val user = LocalUserRepository(_application.applicationContext).partialUser.first()
            period.value = newPeriod
            fetchAll(user.username)
        }
    }
}