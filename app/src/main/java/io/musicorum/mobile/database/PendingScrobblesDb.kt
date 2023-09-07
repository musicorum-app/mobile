package io.musicorum.mobile.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.musicorum.mobile.database.daos.PendingScrobblesDao
import io.musicorum.mobile.models.PendingScrobble
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Database(entities = [PendingScrobble::class], version = 2, exportSchema = false)
abstract class PendingScrobblesDb : RoomDatabase() {
    abstract fun pendingScrobblesDao(): PendingScrobblesDao

    companion object {
        @Volatile
        private var Instance: PendingScrobblesDb? = null

        @OptIn(InternalCoroutinesApi::class)
        fun getDatabase(context: Context): PendingScrobblesDb {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, PendingScrobblesDb::class.java, "pendingScrobbles")
                    .fallbackToDestructiveMigration()
                    .build()
            }.also { Instance = it }
        }
    }
}