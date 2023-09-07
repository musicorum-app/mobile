package io.musicorum.mobile.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.musicorum.mobile.models.CachedScrobble
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedScrobblesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: CachedScrobble)

    @Delete
    suspend fun delete(data: CachedScrobble)

    @Query("SELECT * FROM cachedScrobbles")
    fun getAll(): Flow<List<CachedScrobble>>

    @Query("DELETE FROM cachedScrobbles")
    suspend fun clearAll()
}