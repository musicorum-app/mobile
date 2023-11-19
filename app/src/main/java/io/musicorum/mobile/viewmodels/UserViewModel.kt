package io.musicorum.mobile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumArtistEndpoint
import io.musicorum.mobile.models.FetchPeriod
import io.musicorum.mobile.repositories.LocalUserRepository
import io.musicorum.mobile.serialization.RecentTracks
import io.musicorum.mobile.serialization.TopAlbumsResponse
import io.musicorum.mobile.serialization.TopArtistsResponse
import io.musicorum.mobile.serialization.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    val user by lazy { MutableLiveData<User>() }
    val topArtists by lazy { MutableLiveData<TopArtistsResponse>() }
    val recentTracks by lazy { MutableLiveData<RecentTracks>() }
    val topAlbums by lazy { MutableLiveData<TopAlbumsResponse>() }
    val isRefreshing by lazy { MutableStateFlow(false) }
    val errored by lazy { MutableLiveData(false) }
    val ctx = application
    private val usernameArg = savedStateHandle.get<String>("usernameArg")
    private val localUser = LocalUserRepository(ctx)

    fun refresh() {
        isRefreshing.value = true
        recentTracks.value = null
        getRecentTracks(limit = 4, null)
    }

    fun getUser() = kotlin.runCatching {
        viewModelScope.launch {
            if (usernameArg == null) {
                val localUser = LocalUserRepository(ctx).fetch()
                user.value = localUser
                return@launch
            }
            val userInfo = UserEndpoint.getUser(usernameArg)
            user.value = userInfo
            if (userInfo == null) errored.value = true
        }
    }

    fun getTopArtists(limit: Int?, period: FetchPeriod?) = kotlin.runCatching {
        viewModelScope.launch {
            val username = usernameArg ?: localUser.getUser().username
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

    fun getRecentTracks(limit: Int?, extended: Boolean?) = kotlin.runCatching {
        viewModelScope.launch {
            val username = usernameArg ?: localUser.getUser().username
            val res = UserEndpoint.getRecentTracks(username, null, limit, extended)
            recentTracks.value = res
            isRefreshing.value = false
        }
    }

    fun getTopAlbums(period: FetchPeriod?, limit: Int?) = kotlin.runCatching {
        viewModelScope.launch {
            val username = usernameArg ?: localUser.getUser().username
            val res = UserEndpoint.getTopAlbums(username, period, limit)
            topAlbums.value = res
        }
    }

    init {
        getUser()
        getTopArtists(null, FetchPeriod.MONTH)
        getRecentTracks(limit = 4, null)
        getTopAlbums(FetchPeriod.MONTH, null)
    }
}