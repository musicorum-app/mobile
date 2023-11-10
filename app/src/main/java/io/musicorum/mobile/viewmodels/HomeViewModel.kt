package io.musicorum.mobile.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import io.musicorum.mobile.database.CachedScrobblesDb
import io.musicorum.mobile.database.PendingScrobblesDb
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumTrackEndpoint
import io.musicorum.mobile.models.CachedScrobble
import io.musicorum.mobile.models.FetchPeriod
import io.musicorum.mobile.models.PartialUser
import io.musicorum.mobile.repositories.CachedScrobblesRepository
import io.musicorum.mobile.repositories.LocalUserRepository
import io.musicorum.mobile.repositories.OfflineScrobblesRepository
import io.musicorum.mobile.repositories.ScrobbleRepository
import io.musicorum.mobile.serialization.Image
import io.musicorum.mobile.serialization.RecentTracks
import io.musicorum.mobile.serialization.UserData
import io.musicorum.mobile.serialization.entities.Track
import io.musicorum.mobile.utils.createPalette
import io.musicorum.mobile.utils.getBitmap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
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
    val user = MutableLiveData<PartialUser>()
    val userPalette = MutableLiveData<Palette>()
    val recentTracks = MutableLiveData<List<Track>>()
    val weekTracks = MutableLiveData<List<Track>>()
    val friends = MutableLiveData<List<UserData>>()
    val friendsActivity = MutableLiveData<List<RecentTracks>>()
    val errored = MutableLiveData(false)
    val weeklyScrobbles = MutableLiveData<Int?>(null)
    val isRefreshing = MutableStateFlow(false)
    val isOffline = MutableLiveData(false)
    val hasPendingScrobbles = MutableLiveData(false)
    val showRewindCard = MutableLiveData(false)
    val rewindCardMessage = MutableLiveData("")
    private val remoteConfig = FirebaseRemoteConfig.getInstance()


    fun refresh() {
        isRefreshing.value = true
        recentTracks.value = null
        friends.value = null
        friendsActivity.value = null
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
                isOffline.value = false
                if (res != null) {
                    scrobbleRepository.recentScrobbles.value = res
                    recentTracks.value = scrobbleRepository
                        .recentScrobbles.value
                        ?.recentTracks
                        ?.tracks
                    //?.sortedByDescending { it.date?.uts }
                    weeklyScrobbles.value = res.recentTracks
                        .recentTracksAttributes
                        .total
                        .toInt()
                    isRefreshing.value = false
                    cacheRepository.deleteAll()
                    recentTracks.value?.take(10)?.forEach {
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
                val pendingRepo = OfflineScrobblesRepository(pendingDao)
                val pendingScrobbles = pendingRepo.getAllScrobblesStream().first()
                isOffline.value = true
                val list = mutableListOf<Track>()

                for (s in pendingScrobbles) {
                    list.add(s.toTrack())
                }

                if (pendingScrobbles.isNotEmpty()) {
                    hasPendingScrobbles.value = true
                }
                val cache = cacheRepository.getAllFromCache().first()
                cache.forEach {
                    list.add(it.toTrack())
                }
                list.sortByDescending { it.date?.uts }
                recentTracks.value = list
                weeklyScrobbles.value = 0
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
        val cachedDao = CachedScrobblesDb.getDatabase(ctx).cachedScrobblesDao()
        val cachedRepo = CachedScrobblesRepository(cachedDao)
        viewModelScope.launch {
            val res = kotlin.runCatching {
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
                weekTracks.value = topTracksRes.topTracks.tracks
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
                weekTracks.value = list
            }
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
            val rewindEnabled = remoteConfig.getBoolean("rewind_enabled")
            val firebaseRewindMessage = remoteConfig.getString("rewind_custom_message")
            val rewindMessage = firebaseRewindMessage.ifEmpty {
                "Check out this year's Rewind!"
            }
            showRewindCard.value = rewindEnabled
            rewindCardMessage.value = rewindMessage
        }
    }

    init {
        init()
    }
}