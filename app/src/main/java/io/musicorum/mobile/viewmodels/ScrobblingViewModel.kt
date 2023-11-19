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
import io.musicorum.mobile.database.CachedScrobblesDb
import io.musicorum.mobile.database.PendingScrobblesDb
import io.musicorum.mobile.ktor.endpoints.TrackEndpoint
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.models.CachedScrobble
import io.musicorum.mobile.repositories.CachedScrobblesRepository
import io.musicorum.mobile.repositories.LocalUserRepository
import io.musicorum.mobile.repositories.PendingScrobblesRepository
import io.musicorum.mobile.repositories.ScrobbleRepository
import io.musicorum.mobile.serialization.entities.Track
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class ScrobblingViewModel @Inject constructor(
    private val scrobbleRepository: ScrobbleRepository,
    application: Application
) :
    AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    val ctx = application as Context
    val nowPlayingTrack = MutableLiveData<Track>(null)
    val refreshing = MutableLiveData(false)
    val isTrackLoved = MutableLiveData(false)
    val recentScrobbles = MutableLiveData<List<Track>?>(null)

    fun updateScrobbles() {
        refreshing.value = true
        val cachedScrobblesDao = CachedScrobblesDb.getDatabase(ctx).cachedScrobblesDao()
        val cachedRepo = CachedScrobblesRepository(cachedScrobblesDao)
        viewModelScope.launch {
            val user = LocalUserRepository(ctx).getUser()
            val result = kotlin.runCatching {
                val res = UserEndpoint.getRecentTracks(user.username, null, null, null)
                if (res != null) {
                    scrobbleRepository.updateData(res)
                    cachedRepo.deleteAll()
                    for (t in res.recentTracks.tracks) {
                        cachedRepo.insert(
                            CachedScrobble(
                                trackName = t.name,
                                artistName = t.artist.name,
                                imageUrl = t.bestImageUrl,
                                scrobbleDate = t.date?.uts?.toLong() ?: 0,
                                isTopTrack = false
                            )
                        )
                    }
                    if (res.recentTracks.tracks.isEmpty()) return@launch
                    recentScrobbles.value = res.recentTracks.tracks

                    if (res.recentTracks.tracks[0].attributes?.nowPlaying == "true") {
                        nowPlayingTrack.value = res.recentTracks.tracks[0]
                        if (res.recentTracks.tracks[0].loved) {
                            isTrackLoved.value = true
                        }
                    }
                }
                refreshing.value = false
            }

            if (result.exceptionOrNull() is UnknownHostException) {
                val pendingScrobblesDao = PendingScrobblesDb.getDatabase(ctx).pendingScrobblesDao()
                val pendingRepo = PendingScrobblesRepository(pendingScrobblesDao)
                val list = mutableListOf<Track>()

                val cached = cachedRepo.getAllFromCache().first()
                for (t in cached) {
                    list.add(t.toTrack())
                }

                val offline = pendingRepo.getAllScrobblesStream().first()
                for (t in offline) {
                    list.add(t.toTrack())
                }
                list.sortByDescending { it.date?.uts }
                recentScrobbles.value = list
                refreshing.value = false
            }
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