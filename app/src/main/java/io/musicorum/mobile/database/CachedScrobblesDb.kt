package io.musicorum.mobile.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.musicorum.mobile.models.CachedScrobble
import io.musicorum.mobile.repositories.daos.CachedScrobblesDao
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Database(entities = [CachedScrobble::class], version = 3, exportSchema = false)
abstract class CachedScrobblesDb : RoomDatabase() {
    abstract fun cachedScrobblesDao(): CachedScrobblesDao

    companion object {
        @Volatile
        private var Instance: CachedScrobblesDb? = null

        @OptIn(InternalCoroutinesApi::class)
        fun getDatabase(context: Context): CachedScrobblesDb {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, CachedScrobblesDb::class.java, "cachedScrobbles")
                    .fallbackToDestructiveMigration()
                    .build()
            }.also { Instance = it }
        }
    }
}