package io.musicorum.mobile.views.scrobbling

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.media.session.MediaSessionManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.musicorum.mobile.database.CachedScrobblesDb
import io.musicorum.mobile.database.PendingScrobblesDb
import io.musicorum.mobile.datastore.ScrobblePreferences
import io.musicorum.mobile.ktor.endpoints.TrackEndpoint
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.models.CachedScrobble
import io.musicorum.mobile.repositories.CachedScrobblesRepository
import io.musicorum.mobile.repositories.LocalUserRepository
import io.musicorum.mobile.repositories.PendingScrobblesRepository
import io.musicorum.mobile.repositories.ScrobbleRepository
import io.musicorum.mobile.scrobblePrefs
import io.musicorum.mobile.serialization.entities.Track
import io.musicorum.mobile.services.NotificationListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
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
    val state = MutableStateFlow(ScrobblingState())


    fun updateScrobbles() {
        state.update {
            it.copy(isRefreshing = true)
        }
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
                    state.update {
                        it.copy(recentScrobbles = res.recentTracks.tracks)
                    }

                    if (res.recentTracks.tracks[0].attributes?.nowPlaying == "true") {
                        state.update {
                            it.copy(playingTrack = res.recentTracks.tracks[0])
                        }
                        if (res.recentTracks.tracks[0].loved) {
                            state.update {
                                it.copy(isTrackLoved = true)
                            }
                        }
                    }
                }
                state.update {
                    it.copy(isRefreshing = false)
                }
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
                state.update {
                    it.copy(recentScrobbles = list, isRefreshing = false)
                }
            }
        }
    }

    fun updateFavorite(track: Track?, lovedState: Boolean) {
        if (track == null) return
        viewModelScope.launch {
            TrackEndpoint.updateFavoritePreference(track, lovedState, ctx)
            state.update {
                it.copy(isTrackLoved = !it.isTrackLoved)
            }
        }
    }

    private fun getMediaSessionPackage() = runCatching {
        val mediaService =
            ctx.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        val component = ComponentName(ctx, NotificationListener::class.java)
        val session = mediaService.getActiveSessions(component).getOrNull(0)
        session?.let {
            val pkg = session.packageName
            val pm = ctx.packageManager.getApplicationInfo(pkg, 0)
            val icon = ctx.packageManager.getApplicationIcon(pm)
            viewModelScope.launch {
                val allowedApps =
                    ctx.scrobblePrefs.data.map { p -> p[ScrobblePreferences.ALLOWED_APPS_KEY] }
                        .first() ?: emptySet()

                if (pkg !in allowedApps) return@launch
                state.update {
                    it.copy(
                        scrobblingAppName = pm.loadLabel(ctx.packageManager).toString(),
                        scrobblingAppIcon = icon
                    )
                }
            }
        }
    }

    init {
        FirebaseAnalytics.getInstance(ctx)
            .logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                param(FirebaseAnalytics.Param.SCREEN_NAME, "scrobbling")
            }
        updateScrobbles()
        getMediaSessionPackage()
    }
}