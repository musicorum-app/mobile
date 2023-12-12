package io.musicorum.mobile.views.charts

import android.app.Application
import android.net.ConnectivityManager
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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class ChartsViewModel(application: Application) : AndroidViewModel(application) {
    val preferredColor: MutableLiveData<Color> = MutableLiveData()
    val topArtists: MutableLiveData<List<TopArtist>> = MutableLiveData()
    val topAlbums: MutableLiveData<List<TopAlbum>> = MutableLiveData()
    val topTracks: MutableLiveData<TopTracksData> = MutableLiveData()
    val period = MutableLiveData(FetchPeriod.WEEK)
    val busy = MutableLiveData(false)
    val offline = MutableLiveData(false)
    val _application = application

    init {
        val connectivityManager = application.getSystemService(Application.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        if (connectivityManager.activeNetwork != null) {
            getUserColor()
            viewModelScope.launch {
                fetchAll()
            }
        } else {
            offline.value = true
        }
    }

    private fun getUserColor() {
        viewModelScope.launch {
            val user = LocalUserRepository(_application).getUser()
            val bmp = getBitmap(user.imageUrl, _application)
            val palette = createPalette(bmp)
            if (palette.vibrantSwatch == null) {
                preferredColor.value = Color(palette.getDominantColor(Color.Gray.toArgb()))
            } else {
                preferredColor.value = Color(palette.getVibrantColor(Color.Gray.toArgb()))
            }
        }
    }

    private suspend fun getTopArtists(user: String) {
        val res = UserEndpoint.getTopArtists(username = user, period = period.value, limit = 4)
        res?.let {
            val musRes = MusicorumArtistEndpoint.fetchArtist(res.topArtists.artists)
            it.topArtists.artists.onEachIndexed { i, artist ->
                artist.bestImageUrl = musRes.getOrNull(i)?.bestResource?.bestImageUrl ?: ""
            }
            topArtists.value = it.topArtists.artists
        }
    }

    private suspend fun getTopAlbums(user: String) {
        val res = UserEndpoint.getTopAlbums(user = user, period = period.value, limit = 4)
        res?.let {
            topAlbums.value = it.topAlbums.albums
        }
    }

    private suspend fun getTopTracks(user: String) {
        val res = UserEndpoint.getTopTracks(user = user, period = period.value, limit = 4)
        res?.let {
            val musRes = MusicorumTrackEndpoint.fetchTracks(res.topTracks.tracks)
            res.topTracks.tracks.onEachIndexed { i, t ->
                t.bestImageUrl = musRes.getOrNull(i)?.bestResource?.bestImageUrl ?: ""
            }
            topTracks.value = res.topTracks
        }
    }

    fun fetchAll() {
        val connectivityManager = _application.getSystemService(Application.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        if (connectivityManager.activeNetwork != null) {
            busy.value = true
            offline.value = false
            viewModelScope.launch {
                val user = LocalUserRepository(_application.applicationContext).getUser().username
                runCatching {
                    awaitAll(
                        async {
                            getTopAlbums(user)
                        },
                        async {
                            getTopArtists(user)
                        },
                        async {
                            getTopTracks(user)
                        }
                    )
                }
                busy.value = false
            }
        } else {
            offline.value = true
        }
    }

    fun updatePeriod(newPeriod: FetchPeriod) {
        viewModelScope.launch {
            period.value = newPeriod
            fetchAll()
        }
    }
}