package io.musicorum.mobile.services

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.work.BackoffPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.firebase.analytics.FirebaseAnalytics
import io.musicorum.mobile.database.PendingScrobblesDb
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.repositories.PendingScrobblesRepository
import io.musicorum.mobile.scrobblePrefs
import io.musicorum.mobile.userData
import io.musicorum.mobile.workers.ScrobbleWorker
import io.musicorum.mobile.workers.SyncScrobblesWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

class NotificationListener : NotificationListenerService() {
    private val tag = "NotificationListener"
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.d(tag, "internet connection available. syncing offline scrobbles")
            val work = OneTimeWorkRequestBuilder<SyncScrobblesWorker>()
                .setBackoffCriteria(BackoffPolicy.LINEAR, 1L, TimeUnit.MINUTES)
                .addTag("SYNC_SCROBBLES")
                .build()

            WorkManager.getInstance(applicationContext)
                .enqueueUniqueWork("SYNC_SCROBBLES", ExistingWorkPolicy.KEEP, work)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Log.d(tag, "internet connection lost")
        }
    }
    private lateinit var offlineScrobblesRepo: PendingScrobblesRepository

    override fun onListenerConnected() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        Log.d(tag, "listened connected")
        val connectivityManager =
            getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)
        offlineScrobblesRepo = PendingScrobblesRepository(
            PendingScrobblesDb.getDatabase(applicationContext).pendingScrobblesDao()
        )
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val media =
            applicationContext.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        val component = ComponentName(applicationContext, NotificationListener::class.java)

        val activeSessions = media.getActiveSessions(component)
        val player = activeSessions.getOrNull(0) ?: return
        val track = player.metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)
        val artist = player.metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST)
        val album = player.metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM)

        val scrobbleEnabled = runBlocking {
            applicationContext.scrobblePrefs.data.map { p ->
                p[booleanPreferencesKey("enabled")]
            }.first()
        }
        val apps = runBlocking {
            applicationContext.scrobblePrefs.data.map { p ->
                p[stringSetPreferencesKey("enabledApps")]
            }.first()
        } ?: return

        if (scrobbleEnabled == null || scrobbleEnabled == false) return
        if (sbn?.packageName !in apps) return

        val scrobblePoint = runBlocking {
            applicationContext.scrobblePrefs.data.map { p ->
                p[floatPreferencesKey("scrobblePoint")]
            }.first()
        } ?: return

        val updateNowPlaying = runBlocking {
            applicationContext.scrobblePrefs.data.map { p ->
                p[booleanPreferencesKey("updateNowPlaying")]
            }.first()
        }

        val isPlayerPaused = player.playbackState?.state == PlaybackState.STATE_PAUSED
        val trackDuration = player.metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION)
        val elapsed = player.playbackState?.position

        if (trackDuration == null || trackDuration < 0) return

        val albumArtist =
            if (artist != player.metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)) {
                player.metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)
            } else null

        val timeToScrobble = ((trackDuration / 100) * scrobblePoint) - elapsed!!

        val sessionKey = runBlocking {
            applicationContext.userData.data.map { p ->
                p[stringPreferencesKey("session_key")]
            }.first()
        }

        val analytics = FirebaseAnalytics.getInstance(applicationContext)

        if (artist == null || track == null) {
            val bundle = Bundle()
            bundle.putString("reason", "track or artist is null")
            bundle.putString("artist", artist)
            bundle.putString("track", track)
            analytics.logEvent("device_scrobble_failed", bundle)
            return
        }

        if (!isPlayerPaused && updateNowPlaying == true) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val success = UserEndpoint.updateNowPlaying(
                        track = track,
                        album = album,
                        artist = artist,
                        albumArtist = albumArtist,
                        sessionKey = sessionKey!!
                    )
                    Log.d(tag, "is now playing? $success")
                } catch (_: UnknownHostException) {
                }
            }
        }
        if (timeToScrobble < 0) return
        val workManager = WorkManager.getInstance(applicationContext)

        if (isPlayerPaused) {
            workManager.cancelAllWorkByTag("SCROBBLE")
        } else {
            val data = workDataOf(
                "TRACK_NAME" to track,
                "TRACK_ARTIST" to artist
            )
            val scrobbleWork = OneTimeWorkRequestBuilder<ScrobbleWorker>()
                .setInitialDelay(timeToScrobble.toLong(), TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag("SCROBBLE")
                .build()
            workManager.enqueueUniqueWork("SCROBBLE", ExistingWorkPolicy.REPLACE, scrobbleWork)
        }
    }
}
