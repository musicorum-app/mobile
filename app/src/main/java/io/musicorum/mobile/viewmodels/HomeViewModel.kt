package io.musicorum.mobile.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import io.musicorum.mobile.ktor.endpoints.FetchPeriod
import io.musicorum.mobile.ktor.endpoints.TopTracksEndpoint
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumTrackEndpoint
import io.musicorum.mobile.serialization.RecentTracks
import io.musicorum.mobile.serialization.TopTracks
import io.musicorum.mobile.serialization.User
import io.musicorum.mobile.utils.createPalette
import io.musicorum.mobile.utils.getBitmap
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    val user: MutableLiveData<User> by lazy { MutableLiveData<User>() }
    val userPalette: MutableLiveData<Palette> by lazy { MutableLiveData<Palette>() }
    val recentTracks: MutableLiveData<RecentTracks> by lazy { MutableLiveData<RecentTracks>() }
    val weekTracks: MutableLiveData<TopTracks> by lazy { MutableLiveData<TopTracks>() }

    fun fetchUser(sharedPref: SharedPreferences) {
        viewModelScope.launch {
            Log.d("User view model", "user is ${user.value}")
            val fetchedUser = UserEndpoint().getInfo(sharedPref)
            user.value = fetchedUser
            Log.d("User view model", "user is now ${user.value}")
        }
    }

    fun fetchRecentTracks(username: String, from: String?, limit: Int?) {
        viewModelScope.launch {
            recentTracks.value =
                io.musicorum.mobile.ktor.endpoints.RecentTracksEndpoint()
                    .getRecentTracks(username, from, limit)
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
            musicorumTrRes.forEachIndexed { i, tr ->
                val url = tr.resources?.get(0)?.bestImageUrl
                topTracksRes.topTracks.tracks[i].image?.onEach { img ->
                    img.url = url ?: return@onEach
                }
            }
            weekTracks.value = topTracksRes
        }
    }
}