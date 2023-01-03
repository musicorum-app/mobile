package io.musicorum.mobile.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import dagger.hilt.android.lifecycle.HiltViewModel
import io.musicorum.mobile.ktor.endpoints.FetchPeriod
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumTrackEndpoint
import io.musicorum.mobile.serialization.*
import io.musicorum.mobile.utils.ScrobbleRepository
import io.musicorum.mobile.utils.createPalette
import io.musicorum.mobile.utils.getBitmap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val scrobbleRepository: ScrobbleRepository) :
    ViewModel() {
    val user by lazy { MutableLiveData<User>() }
    val userPalette by lazy { MutableLiveData<Palette>() }
    val recentTracks by lazy { MutableLiveData<RecentTracks>() }
    val weekTracks by lazy { MutableLiveData<TopTracks>() }
    val friends by lazy { MutableLiveData<List<UserData>>() }
    val friendsActivity by lazy { MutableLiveData<List<RecentTracks>>() }
    val errored by lazy { MutableLiveData(false) }
    val isRefreshing by lazy { MutableStateFlow(false) }

    fun refresh() {
        isRefreshing.value = true
        recentTracks.value = null
        friends.value = null
        friendsActivity.value = null
    }

    fun fetchRecentTracks(username: String, from: String?, limit: Int?, extended: Boolean?) {
        viewModelScope.launch {
            val res = UserEndpoint.getRecentTracks(username, from, limit, extended)
            if (res != null) {
                scrobbleRepository.recentScrobbles.value = res
                recentTracks.value = scrobbleRepository.recentScrobbles.value
                isRefreshing.value = false
            }
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
            val topTracksRes = UserEndpoint.getTopTracks(username, period, 10)
            if (topTracksRes == null) {
                errored.value = true
                return@launch
            }
            val musicorumTrRes =
                MusicorumTrackEndpoint.fetchTracks(topTracksRes.topTracks.tracks)
            musicorumTrRes?.forEachIndexed { i, track ->
                val url = track.resources?.getOrNull(0)?.bestImageUrl
                topTracksRes.topTracks.tracks[i].images = listOf(Image("unknown", url ?: ""))
                topTracksRes.topTracks.tracks[i].bestImageUrl = url ?: ""
            }
            weekTracks.value = topTracksRes
        }
    }

    fun fetchFriends(username: String, limit: Int?) {
        viewModelScope.launch {
            val friendsRes = UserEndpoint.getFriends(username, limit)
            if (friendsRes == null) {
                errored.value = true
                return@launch
            }
            friends.value = friendsRes.friends.users
            val mutableList: MutableList<RecentTracks> = mutableListOf()
            friendsRes.friends.users.forEach { user ->
                val friendRecentAct =
                    UserEndpoint.getRecentTracks(user.name, null, 1, false)
                friendRecentAct?.let { mutableList.add(it) }
            }
            friendsActivity.value = mutableList.toList()
        }
    }
}