package io.musicorum.mobile.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.ktor.endpoints.FetchPeriod
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumArtistEndpoint
import io.musicorum.mobile.serialization.RecentTracks
import io.musicorum.mobile.serialization.TopAlbumsResponse
import io.musicorum.mobile.serialization.TopArtistsResponse
import io.musicorum.mobile.serialization.User
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    val user: MutableLiveData<User> by lazy { MutableLiveData<User>() }
    val topArtists: MutableLiveData<TopArtistsResponse> by lazy { MutableLiveData<TopArtistsResponse>() }
    val recentTracks: MutableLiveData<RecentTracks> by lazy { MutableLiveData<RecentTracks>() }
    val topAlbums: MutableLiveData<TopAlbumsResponse> by lazy { MutableLiveData<TopAlbumsResponse>() }

    fun getUser(username: String) {
        viewModelScope.launch {
            val userInfo = UserEndpoint().getUser(username)
            if (userInfo != null) {
                user.value = userInfo
            }
        }
    }

    fun getTopArtists(username: String, limit: Int?, period: FetchPeriod?) {
        viewModelScope.launch {
            val res = UserEndpoint().getTopArtists(username, limit, period)
            if (res != null) {
                val musRes =
                    MusicorumArtistEndpoint().fetchArtist(res.topArtists.artists)
                musRes.forEachIndexed { index, trackResponse ->
                    val trackImageUrl = trackResponse.resources?.getOrNull(0)?.bestImageUrl ?: ""
                    res.topArtists.artists[index].bestImageUrl = trackImageUrl
                }
                topArtists.value = res
            }
        }
    }

    fun getRecentTracks(username: String, limit: Int?, extended: Boolean?) {
        viewModelScope.launch {
            val res = UserEndpoint().getRecentTracks(username, null, limit, extended)
            recentTracks.value = res
        }
    }

    suspend fun getTopAlbums(username: String, period: FetchPeriod?, limit: Int?) {
        val res = UserEndpoint().getTopAlbums(username, period, limit)

        if (res != null) {
            topAlbums.value = res
        }
    }
}