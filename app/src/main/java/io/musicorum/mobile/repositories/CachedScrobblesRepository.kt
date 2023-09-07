package io.musicorum.mobile.repositories

import io.musicorum.mobile.database.daos.CachedScrobblesDao
import io.musicorum.mobile.models.CachedScrobble

class CachedScrobblesRepository(private val cachedScrobblesDao: CachedScrobblesDao) {

    fun getAll() = cachedScrobblesDao.getAll()

    suspend fun delete(data: CachedScrobble) = cachedScrobblesDao.delete(data)

    suspend fun insert(data: CachedScrobble) = cachedScrobblesDao.insert(data)

    suspend fun deleteAll() = cachedScrobblesDao.clearAll()
}