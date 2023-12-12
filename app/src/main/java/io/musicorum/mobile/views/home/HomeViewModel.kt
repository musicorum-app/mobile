package io.musicorum.mobile.views.home

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import io.musicorum.mobile.database.CachedScrobblesDb
import io.musicorum.mobile.database.PendingScrobblesDb
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumTrackEndpoint
import io.musicorum.mobile.models.CachedScrobble
import io.musicorum.mobile.models.FetchPeriod
import io.musicorum.mobile.repositories.CachedScrobblesRepository
import io.musicorum.mobile.repositories.LocalUserRepository
import io.musicorum.mobile.repositories.PendingScrobblesRepository
import io.musicorum.mobile.repositories.ScrobbleRepository
import io.musicorum.mobile.serialization.Image
import io.musicorum.mobile.serialization.RecentTracks
import io.musicorum.mobile.serialization.entities.Track
import io.musicorum.mobile.utils.createPalette
import io.musicorum.mobile.utils.getBitmap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.UnknownHostException
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
    private val remoteConfig = FirebaseRemoteConfig.getInstance()
    val state = MutableStateFlow(HomeState())


    fun refresh() {
        state.update {
            it.copy(
                isRefreshing = true,
                recentTracks = null,
                friends = null,
                friendsActivity = null
            )
        }
        init()
    }

    fun launchRewind() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://rewind.musc.pw")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        ctx.startActivity(intent)
    }

    private fun fetchRecentTracks(username: String, from: String?) {
        viewModelScope.launch {
            val cachedDao = CachedScrobblesDb.getDatabase(ctx).cachedScrobblesDao()
            val cacheRepository = CachedScrobblesRepository(cachedDao)
            val res = kotlin.runCatching {
                val res = UserEndpoint.getRecentTracks(username, from, 15, true)
                state.update { state ->
                    state.copy(isOffline = false)
                }
                if (res != null) {
                    val musRes = MusicorumTrackEndpoint.fetchTracks(res.recentTracks.tracks)
                    res.recentTracks.tracks.onEachIndexed { index, track ->
                        track.bestImageUrl =
                            musRes.getOrNull(index)?.bestResource?.bestImageUrl
                                ?: return@onEachIndexed
                    }
                    scrobbleRepository.recentScrobbles.value = res
                    state.update {
                        it.copy(
                            recentTracks = scrobbleRepository.recentScrobbles.value?.recentTracks?.tracks,
                            weeklyScrobbles =
                            res.recentTracks.recentTracksAttributes.total.toInt(),
                            isRefreshing = false
                        )
                    }
                    cacheRepository.deleteAll()
                    state.value.recentTracks?.take(10)?.forEach {
                        cacheRepository.insert(
                            CachedScrobble(
                                trackName = it.name,
                                artistName = it.artist.name,
                                scrobbleDate = it.date?.uts?.toLong() ?: 0,
                                imageUrl = it.bestImageUrl,
                                isTopTrack = false
                            )
                        )
                    }
                }
            }
            if (res.exceptionOrNull() is UnknownHostException) {
                val pendingDao = PendingScrobblesDb.getDatabase(ctx).pendingScrobblesDao()
                val pendingRepo = PendingScrobblesRepository(pendingDao)
                val pendingScrobbles = pendingRepo.getAllScrobblesStream().first()
                state.update {
                    it.copy(isOffline = true)
                }
                val list = mutableListOf<Track>()

                for (s in pendingScrobbles) {
                    list.add(s.toTrack())
                }

                if (pendingScrobbles.isNotEmpty()) {
                    state.update {
                        it.copy(hasPendingScrobbles = true)
                    }
                }
                val cache = cacheRepository.getAllFromCache().first()
                cache.forEach {
                    list.add(it.toTrack())
                }
                list.sortByDescending { it.date?.uts }
                state.update {
                    it.copy(recentTracks = list, weeklyScrobbles = 0, isRefreshing = false)
                }
            }
        }
    }

    private fun getPalette(imageUrl: String, context: Context) {
        viewModelScope.launch {
            val bmp = getBitmap(imageUrl, context)
            val p = createPalette(bmp)
            state.update {
                it.copy(userPalette = p)
            }
        }
    }

    private fun fetchTopTracks(username: String) {
        val cachedDao = CachedScrobblesDb.getDatabase(ctx).cachedScrobblesDao()
        val cachedRepo = CachedScrobblesRepository(cachedDao)
        viewModelScope.launch {
            val res = kotlin.runCatching {
                val topTracksRes = UserEndpoint.getTopTracks(username, FetchPeriod.WEEK, 10)
                if (topTracksRes == null) {
                    state.update {
                        it.copy(hasError = true)
                    }
                    return@launch
                }
                val musicorumTrRes =
                    MusicorumTrackEndpoint.fetchTracks(topTracksRes.topTracks.tracks)
                musicorumTrRes.forEachIndexed { i, track ->
                    val url = track?.resources?.getOrNull(0)?.bestImageUrl
                    topTracksRes.topTracks.tracks[i].images = listOf(Image("unknown", url ?: ""))
                    topTracksRes.topTracks.tracks[i].bestImageUrl = url ?: ""
                }
                state.update {
                    it.copy(weekTracks = topTracksRes.topTracks.tracks)
                }
                for (t in topTracksRes.topTracks.tracks) {
                    cachedRepo.insert(
                        CachedScrobble(
                            isTopTrack = true,
                            scrobbleDate = t.date?.uts?.toLong() ?: 0,
                            imageUrl = t.bestImageUrl,
                            artistName = t.artist.name,
                            trackName = t.name
                        )
                    )
                }
            }

            if (res.exceptionOrNull() is UnknownHostException) {
                val topTracks = cachedRepo.getAllTopsFromCache().first()
                val list = mutableListOf<Track>()
                for (t in topTracks) {
                    list.add(t.toTrack())
                }
                state.update {
                    it.copy(weekTracks = list)
                }
            }
        }
    }

    private fun fetchFriends(username: String) {
        viewModelScope.launch {
            val friendsRes = UserEndpoint.getFriends(username, 3)
            if (friendsRes == null) {
                state.update {
                    it.copy(hasError = true)
                }
                return@launch
            }
            state.update {
                it.copy(friends = friendsRes.friends.users)
            }
            val mutableList: MutableList<RecentTracks> = mutableListOf()
            friendsRes.friends.users.forEach { user ->
                val friendRecentAct =
                    UserEndpoint.getRecentTracks(user.name, null, 1, false)
                friendRecentAct?.let { mutableList.add(it) }
            }
            state.update {
                it.copy(friendsActivity = mutableList.toList())
            }
        }
    }

    private fun init() {
        val fromTimestamp = "${Instant.now().minusSeconds(604800).toEpochMilli() / 1000}"
        viewModelScope.launch {
            val localUser = LocalUserRepository(ctx).getUser()
            state.update {
                it.copy(user = localUser)
            }
            getPalette(localUser.imageUrl, ctx)
            fetchRecentTracks(localUser.username, fromTimestamp)
            fetchTopTracks(localUser.username)
            fetchFriends(localUser.username)
            val rewindEnabled = remoteConfig.getBoolean("rewind_enabled")
            val firebaseRewindMessage = remoteConfig.getString("rewind_custom_message")
            val rewindMessage = firebaseRewindMessage.ifEmpty {
                "Check out this year's Rewind!"
            }
            state.update { current ->
                current.copy(showRewindCard = rewindEnabled, rewindCardMessage = rewindMessage)
            }

        }
    }

    init {
        init()
        state.update { current ->
            current.copy(showSettingsBade = Firebase.crashlytics.didCrashOnPreviousExecution())
        }
    }
}