package io.musicorum.mobile.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.musicorum.mobile.serialization.Image
import io.musicorum.mobile.serialization.entities.Artist
import io.musicorum.mobile.serialization.entities.Track
import io.musicorum.mobile.serialization.entities.TrackDate

@Entity(tableName = "cachedScrobbles")
data class CachedScrobble(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val trackName: String,
    val artistName: String,
    val scrobbleDate: Long,
    val imageUrl: String,
    val isTopTrack: Boolean
) {
    fun toTrack(): Track {
        return Track(
            name = trackName,
            artist = Artist(name = artistName),
            url = "",
            images = listOf(Image("unknown", imageUrl)),
            date = TrackDate(uts = scrobbleDate.toString())
        )
    }
}
