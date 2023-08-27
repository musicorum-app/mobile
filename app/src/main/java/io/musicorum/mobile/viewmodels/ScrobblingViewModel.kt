package io.musicorum.mobile.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.musicorum.mobile.ktor.endpoints.TrackEndpoint
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.repositories.LocalUserRepository
import io.musicorum.mobile.repositories.ScrobbleRepository
import io.musicorum.mobile.serialization.entities.Track
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScrobblingViewModel @Inject constructor(
    private val scrobbleRepository: ScrobbleRepository,
    application: Application
) :
    AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    val ctx = application as Context
    val recentScrobbles = scrobbleRepository.recentScrobbles
    val refreshing = MutableLiveData(false)
    val isTrackLoved = MutableLiveData(false)

    fun updateScrobbles() {
        refreshing.value = true
        viewModelScope.launch {
            val user = LocalUserRepository(ctx).partialUser.first()
            val res = UserEndpoint.getRecentTracks(user.username, null, null, null)
            if (res != null) {
                scrobbleRepository.updateData(res)
                if (res.recentTracks.tracks[0].attributes?.nowPlaying == "true") {
                    if (res.recentTracks.tracks[0].loved) {
                        isTrackLoved.value = true
                    }
                }
            }
            refreshing.value = false
        }
    }

    fun updateFavorite(track: Track?, lovedState: Boolean) {
        if (track == null) return
        viewModelScope.launch {
            TrackEndpoint.updateFavoritePreference(track, lovedState, ctx)
            isTrackLoved.value = !isTrackLoved.value!!
        }
    }

    init {
        FirebaseAnalytics.getInstance(ctx)
            .logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                param(FirebaseAnalytics.Param.SCREEN_NAME, "scrobbling")
            }
        updateScrobbles()
    }
}