package io.musicorum.mobile.views.individual.user

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.datastore.UserData
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumArtistEndpoint
import io.musicorum.mobile.models.FetchPeriod
import io.musicorum.mobile.repositories.LocalUserRepository
import io.musicorum.mobile.userData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    val state = MutableStateFlow(UserState())
    val errored by lazy { MutableLiveData(false) }
    val ctx = application
    private val usernameArg = savedStateHandle.get<String>("usernameArg")
    private val localUser = LocalUserRepository(ctx)

    fun refresh() {
        state.update {
            it.copy(isRefreshing = true, recentTracks = null)
        }
        getRecentTracks(limit = 4, null)
    }

    fun getUser() = kotlin.runCatching {
        viewModelScope.launch {
            if (usernameArg == null) {
                val user = localUser.fetch()
                state.update {
                    it.copy(user = user)
                }
            } else {
                val userInfo = UserEndpoint.getUser(usernameArg)
                state.update {
                    it.copy(user = userInfo, hasError = userInfo == null)
                }
            }
        }
    }

    private fun getTopArtists(limit: Int?, period: FetchPeriod?) = kotlin.runCatching {
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
                state.update {
                    it.copy(topArtists = res.topArtists.artists)
                }
            }
        }
    }

    private fun getRecentTracks(limit: Int?, extended: Boolean?) = kotlin.runCatching {
        viewModelScope.launch {
            val username = usernameArg ?: localUser.getUser().username
            val res = UserEndpoint.getRecentTracks(username, null, limit, extended)
            state.update {
                it.copy(recentTracks = res?.recentTracks?.tracks, isRefreshing = false)
            }
        }
    }

    private fun getTopAlbums(period: FetchPeriod?, limit: Int?) = kotlin.runCatching {
        viewModelScope.launch {
            val username = usernameArg ?: localUser.getUser().username
            val res = UserEndpoint.getTopAlbums(username, period, limit)
            state.update {
                it.copy(topAlbums = res?.topAlbums?.albums)
            }
        }
    }

    private fun updatePinState() {
        if (usernameArg == null) {
            state.update {
                it.copy(showPin = false)
            }
        } else {
            viewModelScope.launch {
                val pinnedUsers = ctx.userData.data.map {
                    it[UserData.PINNED_USERS]
                }.first() ?: emptySet()
                state.update {
                    it.copy(isPinned = usernameArg in pinnedUsers, canPin = pinnedUsers.size < 3)
                }
            }
        }
    }

    fun updatePin() {
        viewModelScope.launch {
            val currentPinned = ctx.userData.data.map {
                it[UserData.PINNED_USERS]
            }.first() ?: emptySet()

            val newPinned = if (usernameArg in currentPinned) {
                currentPinned - usernameArg!!
            } else {
                currentPinned + usernameArg!!
            }
            ctx.userData.edit {
                it[UserData.PINNED_USERS] = newPinned
            }
            state.update {
                it.copy(isPinned = usernameArg in newPinned)
            }
        }
    }

    init {
        getUser()
        getTopArtists(null, FetchPeriod.MONTH)
        getRecentTracks(limit = 4, null)
        getTopAlbums(FetchPeriod.MONTH, null)
        updatePinState()
    }
}