package io.musicorum.mobile.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.ktor.http.HttpStatusCode
import io.musicorum.mobile.database.PendingScrobblesDb
import io.musicorum.mobile.datastore.UserData
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.models.PendingScrobble
import io.musicorum.mobile.repositories.PendingScrobblesRepository
import io.musicorum.mobile.userData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Date

class ScrobbleWorker(val ctx: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(ctx, workerParameters) {
    override suspend fun doWork(): Result {
        val trackName = inputData.getString("TRACK_NAME")
        val trackArtist = inputData.getString("TRACK_ARTIST")

        if (trackName == null || trackArtist == null) {
            return Result.failure()
        }

        val sessionKey = ctx.userData.data.map {
            it[UserData.SESSION_KEY]
        }.first() ?: return Result.failure()

        val timestamp = Date().time

        val res = UserEndpoint.scrobble(
            track = trackName,
            artist = trackArtist,
            sessionKey = sessionKey,
            timestamp = Date().time / 1000
        )

        return if (res == HttpStatusCode.OK) {
            Log.d("ScrobbleWorker", "scrobble succeeded")
            Result.success()
        } else {
            val pendingScrobble = PendingScrobble(
                trackName = trackName,
                artistName = trackArtist,
                timestamp = timestamp,
                album = null
            )
            val pendingDao = PendingScrobblesDb.getDatabase(ctx).pendingScrobblesDao()
            PendingScrobblesRepository(pendingDao)
                .insertScrobble(pendingScrobble)
            Log.d("ScrobbleWorker", "scrobble has been saved offline")
            Result.failure()
        }
    }
}