package io.musicorum.mobile

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Date

class NotificationListener : NotificationListenerService() {
    private val tag = "NotificationListener"
    private var job: Job? = null

    override fun onListenerConnected() {
        Log.d(tag, "listened connected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
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
        if (packageName !in apps) return

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

        val media =
            applicationContext.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        val component = ComponentName(applicationContext, NotificationListener::class.java)

        val player = media.getActiveSessions(component)[0]
        val isPlayerPaused = player.playbackState?.state == PlaybackState.STATE_PAUSED
        val trackDuration = player.metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION)
        val elapsed = player.playbackState?.position

        if (trackDuration == null || trackDuration < 0) return

        val track = player.metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)
        val artist = player?.metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST)
        val album = player?.metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM)
        val albumArtist =
            if (artist != player?.metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)) {
                player?.metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)
            } else null

        val timeToScrobble = ((trackDuration / 100) * scrobblePoint) - elapsed!!

        if (timeToScrobble < 0) return

        val sessionKey = runBlocking {
            applicationContext.userData.data.map { p ->
                p[stringPreferencesKey("session_key")]
            }.first()
        }

        val timestamp = Date()

        if (!isPlayerPaused && updateNowPlaying == true) {
            CoroutineScope(Dispatchers.IO).launch {
                val success = UserEndpoint.updateNowPlaying(
                    track = track!!,
                    album = album,
                    artist = artist!!,
                    albumArtist = albumArtist,
                    sessionKey = sessionKey!!
                )
                Log.d(tag, "is now playing? $success")
            }
        }

        job = if (isPlayerPaused) {
            Log.d(tag, "player has been paused")
            job?.cancel()
            if (job?.isCancelled == true) Log.d(tag, "job has been cancelled")
            null
        } else {
            job?.cancel()
            Log.d(tag, "lauching new job...")
            CoroutineScope(Dispatchers.IO).launch {
                Log.d("listener job", "this job will wait ${timeToScrobble / 1000} seconds.")
                delay(timeToScrobble.toLong())
                Log.d("listener job", "time reached - scrobbling.")
                val req = UserEndpoint.scrobble(
                    track = track!!,
                    artist = artist!!,
                    album = album,
                    albumArtist = albumArtist,
                    sessionKey = sessionKey!!,
                    timestamp = timestamp.time / 1000
                )
                val success = req.status.isSuccess()
                Log.d(tag, "is scrobble success? $success")
                if (success) {
                    Firebase.analytics.logEvent("device_scrobble_success", null)
                } else {
                    val bundle = Bundle()
                    bundle.putInt("status_code", req.status.value)
                    bundle.putString("body", req.bodyAsText())
                    bundle.putString("attempted_song", "$track by $artist, on $album")
                    Firebase.analytics.logEvent("device_scrobble_failed", bundle)
                }
            }
        }
        Log.d(tag, "----------")
    }
}