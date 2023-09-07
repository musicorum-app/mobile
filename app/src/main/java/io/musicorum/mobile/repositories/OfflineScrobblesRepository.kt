package io.musicorum.mobile.repositories

import io.musicorum.mobile.database.daos.PendingScrobblesDao
import io.musicorum.mobile.models.PendingScrobble
import kotlinx.coroutines.flow.Flow

class OfflineScrobblesRepository(private val scrobblesDao: PendingScrobblesDao) :
    PendingScrobblesRepository {
    override suspend fun getAllScrobblesStream(): Flow<List<PendingScrobble>> =
        scrobblesDao.getAll()

    override suspend fun deleteScrobble(scrobble: PendingScrobble) = scrobblesDao.delete(scrobble)

    override suspend fun insertScrobble(scrobble: PendingScrobble) = scrobblesDao.insert(scrobble)
}