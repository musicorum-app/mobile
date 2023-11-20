package io.musicorum.mobile.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import io.musicorum.mobile.database.PendingScrobblesDb
import io.musicorum.mobile.datastore.UserData
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.repositories.PendingScrobblesRepository
import io.musicorum.mobile.userData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SyncScrobblesWorker(val ctx: Context, workerParams: WorkerParameters) :
    CoroutineWorker(ctx, workerParams) {
    //val context = ctx

    override suspend fun doWork(): Result {
        val pendingDatabase = PendingScrobblesDb.getDatabase(ctx)
        val repository = PendingScrobblesRepository(pendingDatabase.pendingScrobblesDao())
        val sessionKey = ctx.userData.data.map {
            it[UserData.SESSION_KEY]
        }.first() ?: return Result.failure()

        val scrobbles = repository.getAllScrobblesStream().first()
        try {
            if (scrobbles.isEmpty()) {
                return Result.success()
            }
            for (scrobble in scrobbles) {
                val code = UserEndpoint.scrobble(
                    track = scrobble.trackName,
                    artist = scrobble.artistName,
                    album = scrobble.album,
                    albumArtist = scrobble.artistName,
                    sessionKey = sessionKey,
                    timestamp = scrobble.timestamp / 1000
                )

                if (code == HttpStatusCode.OK) {
                    repository.deleteScrobble(scrobble)
                }
            }
            return Result.success()
        } catch (e: ClientRequestException) {
            return if (e.response.status == HttpStatusCode.TooManyRequests) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}