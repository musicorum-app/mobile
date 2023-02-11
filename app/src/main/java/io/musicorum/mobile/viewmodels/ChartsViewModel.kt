package io.musicorum.mobile.viewmodels

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.ktor.endpoints.FetchPeriod
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumArtistEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumTrackEndpoint
import io.musicorum.mobile.serialization.TopAlbum
import io.musicorum.mobile.serialization.TopArtist
import io.musicorum.mobile.serialization.TopTracksData
import io.musicorum.mobile.serialization.Track
import io.musicorum.mobile.utils.createPalette
import io.musicorum.mobile.utils.getBitmap
import kotlinx.coroutines.launch

class ChartsViewModel : ViewModel() {
    val preferredColor: MutableLiveData<Color> = MutableLiveData()
    val topArtists: MutableLiveData<List<TopArtist>> = MutableLiveData()
    val topAlbums: MutableLiveData<List<TopAlbum>> = MutableLiveData()
    val topTracks: MutableLiveData<TopTracksData> = MutableLiveData()

    fun getColor(image: String, ctx: Context) {
        viewModelScope.launch {
            val bmp = getBitmap(image, ctx)
            val palette = createPalette(bmp)
            preferredColor.value = Color(palette.getVibrantColor(Color.Gray.toArgb()))
        }
    }

    fun getTopArtists(user: String, period: FetchPeriod) {
        viewModelScope.launch {
            val res = UserEndpoint.getTopArtists(username = user, period = period, limit = 4)
            res?.let {
                val musRes = MusicorumArtistEndpoint.fetchArtist(res.topArtists.artists)
                it.topArtists.artists.onEachIndexed { i, artist ->
                    artist.bestImageUrl = musRes?.getOrNull(i)?.bestResource?.bestImageUrl ?: ""
                }
                topArtists.value = it.topArtists.artists
            }
        }
    }

    fun getTopAlbums(user: String, period: FetchPeriod) {
        viewModelScope.launch {
            val res = UserEndpoint.getTopAlbums(user = user, period = period, limit = 4)
            res?.let {
                topAlbums.value = it.topAlbums.albums
            }
        }
    }

    fun getTopTracks(user: String, period: FetchPeriod) {
        viewModelScope.launch {
            val res = UserEndpoint.getTopTracks(user = user, period = period, limit = 4)
            res?.let {
                val musRes = MusicorumTrackEndpoint.fetchTracks(res.topTracks.tracks)
                res.topTracks.tracks.onEachIndexed { i, t ->
                    t.bestImageUrl = musRes?.getOrNull(i)?.bestResource?.bestImageUrl ?: ""
                }

                topTracks.value = res.topTracks
            }
        }
    }

    fun invalidate() {
        topArtists.value = null
        topAlbums.value = null
        topTracks.value = null
    }
}