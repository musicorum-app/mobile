package io.musicorum.mobile.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumArtistEndpoint
import io.musicorum.mobile.models.FetchPeriod
import io.musicorum.mobile.serialization.RecentTracks
import io.musicorum.mobile.serialization.TopAlbumsResponse
import io.musicorum.mobile.serialization.TopArtistsResponse
import io.musicorum.mobile.serialization.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    val user by lazy { MutableLiveData<User>() }
    val topArtists by lazy { MutableLiveData<TopArtistsResponse>() }
    val recentTracks by lazy { MutableLiveData<RecentTracks>() }
    val topAlbums by lazy { MutableLiveData<TopAlbumsResponse>() }
    val isRefreshing by lazy { MutableStateFlow(false) }
    val errored by lazy { MutableLiveData(false) }

    fun refresh() {
        isRefreshing.value = true
        recentTracks.value = null
    }

    fun getUser(username: String) = kotlin.runCatching {
        viewModelScope.launch {
            val userInfo = UserEndpoint.getUser(username)
            user.value = userInfo
            if (userInfo == null) errored.value = true
        }
    }

    fun getTopArtists(username: String, limit: Int?, period: FetchPeriod?) = kotlin.runCatching {
        viewModelScope.launch {
            val res = UserEndpoint.getTopArtists(username, limit, period)
            if (res != null) {
                val musRes =
                    MusicorumArtistEndpoint.fetchArtist(res.topArtists.artists)
                musRes.forEachIndexed { index, trackResponse ->
                    val trackImageUrl = trackResponse.resources.getOrNull(0)?.bestImageUrl ?: ""
                    res.topArtists.artists[index].bestImageUrl = trackImageUrl
                }
                topArtists.value = res
            }
        }
    }

    fun getRecentTracks(username: String, limit: Int?, extended: Boolean?) = kotlin.runCatching {
        viewModelScope.launch {
            val res = UserEndpoint.getRecentTracks(username, null, limit, extended)
            recentTracks.value = res
            isRefreshing.value = false
        }
    }

    fun getTopAlbums(username: String, period: FetchPeriod?, limit: Int?) = kotlin.runCatching {
        viewModelScope.launch {
            val res = UserEndpoint.getTopAlbums(username, period, limit)
            topAlbums.value = res
        }
    }
}