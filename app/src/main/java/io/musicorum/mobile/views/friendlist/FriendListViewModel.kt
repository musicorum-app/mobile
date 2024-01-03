package io.musicorum.mobile.views.friendlist

import android.app.Application
import androidx.compose.material3.SnackbarHostState
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.datastore.UserData
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.repositories.LocalUserRepository
import io.musicorum.mobile.userData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import io.musicorum.mobile.serialization.UserData as LastfmUserData

class FriendListViewModel(application: Application) : AndroidViewModel(application) {
    val state = MutableStateFlow(FriendListState())
    private val ctx = application
    val snackbarHostState = SnackbarHostState()

    private fun fetchFriends() {
        viewModelScope.launch {
            state.update {
                it.copy(loading = true)
            }
            val localUser = LocalUserRepository(getApplication()).getUser()
            val res = UserEndpoint.getFriends(localUser.username, null)
            res?.let {
                state.update {
                    it.copy(friends = res.friends.users, loading = false)
                }
            }
        }
    }

    fun pinUser(userData: LastfmUserData) {
        viewModelScope.launch {
            val currentSet = ctx.userData.data.map {
                it[UserData.PINNED_USERS] ?: emptySet()
            }.first().toMutableSet()

            currentSet.add(userData.name)

            ctx.userData.edit {
                it[UserData.PINNED_USERS] = currentSet.toSet()
            }
            state.update {
                it.copy(pinnedUsers = currentSet.toSet())
            }
            snackbarHostState.showSnackbar("${userData.name} pinned on home screen")
        }
    }

    fun unpinUser(userData: LastfmUserData) {
        viewModelScope.launch {
            val currentSet = ctx.userData.data.map {
                it[UserData.PINNED_USERS] ?: emptySet()
            }.first().toMutableSet()

            currentSet.remove(userData.name)

            ctx.userData.edit {
                it[UserData.PINNED_USERS] = currentSet.toSet()
            }

            state.update {
                it.copy(pinnedUsers = currentSet.toSet())
            }
            snackbarHostState.showSnackbar("${userData.name} removed from pinned users")
        }
    }

    init {
        fetchFriends()
        viewModelScope.launch {
            val pinnedUsers = ctx.userData.data.map {
                it[UserData.PINNED_USERS] ?: emptySet()
            }.first()
            state.update {
                it.copy(pinnedUsers = pinnedUsers)
            }
        }
    }
}