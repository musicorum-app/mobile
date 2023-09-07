package io.musicorum.mobile.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.musicorum.mobile.models.PendingScrobble
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingScrobblesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(scrobble: PendingScrobble)

    @Delete
    suspend fun delete(scrobble: PendingScrobble)

    @Query("SELECT * FROM pendingScrobbles")
    fun getAll(): Flow<List<PendingScrobble>>
}