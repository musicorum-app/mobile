package io.musicorum.mobile.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import io.musicorum.mobile.ktor.endpoints.*
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumTrackEndpoint
import io.musicorum.mobile.serialization.*
import io.musicorum.mobile.utils.createPalette
import io.musicorum.mobile.utils.getBitmap
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    val user: MutableLiveData<User> by lazy { MutableLiveData<User>() }
    val userPalette: MutableLiveData<Palette> by lazy { MutableLiveData<Palette>() }
    val recentTracks: MutableLiveData<RecentTracks> by lazy { MutableLiveData<RecentTracks>() }
    val weekTracks: MutableLiveData<TopTracks> by lazy { MutableLiveData<TopTracks>() }
    val friends: MutableLiveData<List<UserData>> by lazy { MutableLiveData<List<UserData>>() }
    val friendsActivity: MutableLiveData<List<RecentTracks>> by lazy { MutableLiveData<List<RecentTracks>>() }

    fun fetchUser(sessionKey: String) {
        viewModelScope.launch {
            Log.d("User view model", "user is ${user.value}")
            val fetchedUser = UserEndpoint().getInfo(sessionKey)
            user.value = fetchedUser
            Log.d("User view model", "user is now ${user.value}")
        }
    }

    fun fetchRecentTracks(username: String, from: String?, limit: Int?, extended: Boolean?) {
        viewModelScope.launch {
            recentTracks.value =
                RecentTracksEndpoint()
                    .getRecentTracks(username, from, limit, extended)
        }
    }

    fun getPalette(imageUrl: String, context: Context) {
        viewModelScope.launch {
            val bmp = getBitmap(imageUrl, context)
            val p = createPalette(bmp)
            userPalette.value = p
        }
    }

    fun fetchTopTracks(username: String, period: FetchPeriod) {
        viewModelScope.launch {
            val topTracksRes = TopTracksEndpoint().fetchTopTracks(username, period, 10)
            //weekTracks.value = topTracksRes
            val musicorumTrRes = MusicorumTrackEndpoint().fetchTracks(topTracksRes.topTracks.tracks)
            musicorumTrRes.forEachIndexed { i, track ->
                val url = track.resources?.getOrNull(0)?.bestImageUrl
                topTracksRes.topTracks.tracks[i].images = listOf(Image("unknown", url ?: ""))
                topTracksRes.topTracks.tracks[i].bestImageUrl = url ?: ""
            }
            weekTracks.value = topTracksRes
        }
    }

    fun fetchFriends(username: String, limit: Int?) {
        viewModelScope.launch {
            val friendsRes = FriendsEndpoint().fetchFriends(username, limit)
            friends.value = friendsRes.friends.users
            val mutableList: MutableList<RecentTracks> = mutableListOf()
            friendsRes.friends.users.forEach { user ->
                val friendRecentAct =
                    RecentTracksEndpoint().getRecentTracks(user.name, null, 1, false)
                mutableList.add(friendRecentAct)
            }
            friendsActivity.value = mutableList.toList()
        }
    }
}