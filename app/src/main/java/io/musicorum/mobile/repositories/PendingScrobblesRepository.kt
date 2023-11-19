package io.musicorum.mobile.repositories

import io.musicorum.mobile.models.PendingScrobble
import io.musicorum.mobile.repositories.daos.PendingScrobblesDao
import kotlinx.coroutines.flow.Flow

class PendingScrobblesRepository(private val scrobblesDao: PendingScrobblesDao) :
    IPendingScrobbles {
    override suspend fun getAllScrobblesStream(): Flow<List<PendingScrobble>> =
        scrobblesDao.getAll()

    override suspend fun deleteScrobble(scrobble: PendingScrobble) = scrobblesDao.delete(scrobble)

    override suspend fun insertScrobble(scrobble: PendingScrobble) = scrobblesDao.insert(scrobble)
}