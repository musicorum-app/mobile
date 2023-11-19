package io.musicorum.mobile.repositories

import io.musicorum.mobile.models.PendingScrobble
import kotlinx.coroutines.flow.Flow

interface IPendingScrobbles {
    suspend fun getAllScrobblesStream(): Flow<List<PendingScrobble>>

    suspend fun deleteScrobble(scrobble: PendingScrobble)

    suspend fun insertScrobble(scrobble: PendingScrobble)
}