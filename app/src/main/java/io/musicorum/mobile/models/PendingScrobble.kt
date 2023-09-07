package io.musicorum.mobile.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.musicorum.mobile.serialization.entities.Artist
import io.musicorum.mobile.serialization.entities.Track
import io.musicorum.mobile.serialization.entities.TrackDate

@Entity(tableName = "pendingScrobbles")
data class PendingScrobble(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val trackName: String,
    val artistName: String,
    val timestamp: Long,
    val album: String?

) {
    fun toTrack(): Track {
        return Track(
            name = trackName,
            pending = true,
            artist = Artist(name = artistName),
            url = "",
            date = TrackDate(uts = (timestamp / 1000).toString())
        )
    }
}
