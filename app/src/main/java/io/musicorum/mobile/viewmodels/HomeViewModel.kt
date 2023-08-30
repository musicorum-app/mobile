package io.musicorum.mobile.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import dagger.hilt.android.lifecycle.HiltViewModel
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumTrackEndpoint
import io.musicorum.mobile.models.FetchPeriod
import io.musicorum.mobile.models.PartialUser
import io.musicorum.mobile.repositories.LocalUserRepository
import io.musicorum.mobile.repositories.ScrobbleRepository
import io.musicorum.mobile.serialization.Image
import io.musicorum.mobile.serialization.RecentTracks
import io.musicorum.mobile.serialization.UserData
import io.musicorum.mobile.serialization.entities.TopTracks
import io.musicorum.mobile.utils.createPalette
import io.musicorum.mobile.utils.getBitmap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val scrobbleRepository: ScrobbleRepository,
    application: Application
) :
    AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    val ctx = application as Context
    val user = MutableLiveData<PartialUser>()
    val userPalette = MutableLiveData<Palette>()
    val recentTracks = MutableLiveData<RecentTracks>()
    val weekTracks = MutableLiveData<TopTracks>()
    val friends = MutableLiveData<List<UserData>>()
    val friendsActivity = MutableLiveData<List<RecentTracks>>()
    val errored = MutableLiveData(false)
    val isRefreshing = MutableStateFlow(false)

    fun refresh() {
        isRefreshing.value = true
        recentTracks.value = null
        friends.value = null
        friendsActivity.value = null
        init()
    }

    private fun fetchRecentTracks(username: String, from: String?) {
        viewModelScope.launch {
            val res = UserEndpoint.getRecentTracks(username, from, 15, true)
            if (res != null) {
                scrobbleRepository.recentScrobbles.value = res
                recentTracks.value = scrobbleRepository.recentScrobbles.value
                isRefreshing.value = false
            }
        }
    }

    private fun getPalette(imageUrl: String, context: Context) {
        viewModelScope.launch {
            val bmp = getBitmap(imageUrl, context)
            val p = createPalette(bmp)
            userPalette.value = p
        }
    }

    private fun fetchTopTracks(username: String) {
        viewModelScope.launch {
            val topTracksRes = UserEndpoint.getTopTracks(username, FetchPeriod.WEEK, 10)
            if (topTracksRes == null) {
                errored.value = true
                return@launch
            }
            val musicorumTrRes =
                MusicorumTrackEndpoint.fetchTracks(topTracksRes.topTracks.tracks)
            musicorumTrRes.forEachIndexed { i, track ->
                val url = track?.resources?.getOrNull(0)?.bestImageUrl
                topTracksRes.topTracks.tracks[i].images = listOf(Image("unknown", url ?: ""))
                topTracksRes.topTracks.tracks[i].bestImageUrl = url ?: ""
            }
            weekTracks.value = topTracksRes
        }
    }

    private fun fetchFriends(username: String) {
        viewModelScope.launch {
            val friendsRes = UserEndpoint.getFriends(username, 3)
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

    private fun init() {

        val fromTimestamp = "${Instant.now().minusSeconds(604800).toEpochMilli() / 1000}"
        viewModelScope.launch {
            val localUser = LocalUserRepository(ctx).getUser()
            user.value = localUser
            getPalette(localUser.imageUrl, ctx)

            fetchRecentTracks(localUser.username, fromTimestamp)
            fetchTopTracks(localUser.username)
            fetchFriends(localUser.username)
        }
    }

    init {
        init()
    }
}