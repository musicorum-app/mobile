package io.musicorum.mobile.repositories

import android.os.SystemClock
import io.musicorum.mobile.serialization.SimilarTrack
import io.musicorum.mobile.serialization.entities.Track
import javax.inject.Inject
import javax.inject.Singleton

internal data class TrackDetailsCacheKey(
    val username: String,
    val artist: String,
    val track: String
) {
    companion object {
        fun create(username: String, artist: String, track: String): TrackDetailsCacheKey {
            return TrackDetailsCacheKey(
                username = username.normalizedCacheValue(),
                artist = artist.normalizedCacheValue(),
                track = track.normalizedCacheValue()
            )
        }
    }
}

internal data class TrackDetailsCacheEntry(
    val track: Track,
    val trackUpdatedAt: Long,
    val albumArtworkUpdatedAt: Long?,
    val artistArtworkUpdatedAt: Long?,
    val similar: SimilarTrack? = null,
    val similarUpdatedAt: Long? = null
)

@Singleton
class TrackDetailsCache internal constructor(
    private val maxEntries: Int,
    private val clock: () -> Long
) {
    @Inject
    constructor() : this(
        maxEntries = DetailCachePolicy.MAX_ENTRIES,
        clock = SystemClock::elapsedRealtime
    )

    private val entries = BoundedLruCache<TrackDetailsCacheKey, TrackDetailsCacheEntry>(maxEntries)

    internal fun get(key: TrackDetailsCacheKey): TrackDetailsCacheEntry? {
        return entries[key]
    }

    internal fun isTrackFresh(entry: TrackDetailsCacheEntry): Boolean {
        return isFresh(
            entry.trackUpdatedAt,
            DetailCachePolicy.DETAILS_FRESHNESS_MILLIS
        )
    }

    internal fun isAlbumArtworkFresh(entry: TrackDetailsCacheEntry): Boolean {
        val updatedAt = entry.albumArtworkUpdatedAt ?: return false
        return isFresh(
            updatedAt,
            DetailCachePolicy.ARTWORK_FRESHNESS_MILLIS
        )
    }

    internal fun isArtistArtworkFresh(entry: TrackDetailsCacheEntry): Boolean {
        val updatedAt = entry.artistArtworkUpdatedAt ?: return false
        return isFresh(
            updatedAt,
            DetailCachePolicy.ARTWORK_FRESHNESS_MILLIS
        )
    }

    internal fun isSimilarFresh(entry: TrackDetailsCacheEntry): Boolean {
        val updatedAt = entry.similarUpdatedAt ?: return false
        return entry.similar != null && isFresh(
            updatedAt,
            DetailCachePolicy.RELATED_FRESHNESS_MILLIS
        )
    }

    @Synchronized
    internal fun putTrack(
        key: TrackDetailsCacheKey,
        track: Track,
        albumArtworkRefreshed: Boolean,
        artistArtworkRefreshed: Boolean
    ) {
        val cached = entries[key]
        val now = clock()
        entries.put(
            key,
            TrackDetailsCacheEntry(
                track = track,
                trackUpdatedAt = now,
                albumArtworkUpdatedAt = if (albumArtworkRefreshed) {
                    now
                } else {
                    cached?.albumArtworkUpdatedAt
                },
                artistArtworkUpdatedAt = if (artistArtworkRefreshed) {
                    now
                } else {
                    cached?.artistArtworkUpdatedAt
                },
                similar = cached?.similar,
                similarUpdatedAt = cached?.similarUpdatedAt
            )
        )
    }

    @Synchronized
    internal fun putSimilar(key: TrackDetailsCacheKey, similar: SimilarTrack) {
        val cached = entries[key] ?: return
        entries.put(
            key,
            cached.copy(
                similar = similar,
                similarUpdatedAt = clock()
            )
        )
    }

    @Synchronized
    internal fun updateTrack(key: TrackDetailsCacheKey, track: Track) {
        val cached = entries[key] ?: return
        entries.put(key, cached.copy(track = track))
    }

    internal fun isFresh(updatedAt: Long, freshnessMillis: Long): Boolean {
        return isCacheValueFresh(clock(), updatedAt, freshnessMillis)
    }
}
