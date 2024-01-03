package io.musicorum.mobile.views.friendlist

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumTrackEndpoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FriendActivityViewModel(application: Application) : AndroidViewModel(application) {
    val state = MutableStateFlow(FriendActivityState())
    val app = application

    fun fetchActivity(username: String) {
        if (state.value.track != null) return
        viewModelScope.launch {
            state.update {
                it.copy(loading = true)
            }
            val res = UserEndpoint.getRecentTracks(username, null, 1)
            res?.let {
                val track = res.recentTracks.tracks.first()
                state.update {
                    it.copy(
                        track = track,
                        loading = false,
                        nowPlaying = track.attributes?.nowPlaying?.toBooleanStrictOrNull() ?: false
                    )
                }
                val musRes = MusicorumTrackEndpoint.fetchTracks(res.recentTracks.tracks)
                res.recentTracks.tracks.onEach {
                    musRes[0]?.let { r ->
                        it.bestImageUrl = r.bestResource?.bestImageUrl ?: ""
                    }
                }

                state.update {
                    it.copy(
                        track = track,
                        loading = false,
                        nowPlaying = track.attributes?.nowPlaying?.toBooleanStrictOrNull() ?: false
                    )
                }
            }
        }
    }

    fun launchWebProfile(username: String) {
        val uri = Uri.parse("https://last.fm/user/$username")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        app.startActivity(intent)
    }
}