package io.musicorum.mobile.repositories

import android.os.SystemClock
import io.musicorum.mobile.serialization.TopAlbum
import io.musicorum.mobile.serialization.entities.Artist
import io.musicorum.mobile.serialization.entities.Track
import javax.inject.Inject
import javax.inject.Singleton

internal data class ArtistDetailsCacheKey(
    val username: String,
    val artist: String
) {
    companion object {
        fun create(username: String, artist: String): ArtistDetailsCacheKey {
            return ArtistDetailsCacheKey(
                username = username.normalizedCacheValue(),
                artist = artist.normalizedCacheValue()
            )
        }
    }
}

internal data class ArtistDetailsCacheEntry(
    val artist: Artist,
    val artistUpdatedAt: Long,
    val artworkUpdatedAt: Long?,
    val topAlbums: List<TopAlbum>? = null,
    val topAlbumsUpdatedAt: Long? = null,
    val topTracks: List<Track>? = null,
    val topTracksUpdatedAt: Long? = null
)

@Singleton
class ArtistDetailsCache internal constructor(
    maxEntries: Int,
    private val clock: () -> Long
) {
    @Inject
    constructor() : this(
        maxEntries = DetailCachePolicy.MAX_ENTRIES,
        clock = SystemClock::elapsedRealtime
    )

    private val entries = BoundedLruCache<ArtistDetailsCacheKey, ArtistDetailsCacheEntry>(maxEntries)

    internal fun get(key: ArtistDetailsCacheKey): ArtistDetailsCacheEntry? = entries[key]

    internal fun isArtistFresh(entry: ArtistDetailsCacheEntry): Boolean {
        return isFresh(entry.artistUpdatedAt, DetailCachePolicy.DETAILS_FRESHNESS_MILLIS)
    }

    internal fun isArtworkFresh(entry: ArtistDetailsCacheEntry): Boolean {
        val updatedAt = entry.artworkUpdatedAt ?: return false
        return isFresh(updatedAt, DetailCachePolicy.ARTWORK_FRESHNESS_MILLIS)
    }

    internal fun areTopAlbumsFresh(entry: ArtistDetailsCacheEntry): Boolean {
        val updatedAt = entry.topAlbumsUpdatedAt ?: return false
        return entry.topAlbums != null &&
            isFresh(updatedAt, DetailCachePolicy.RELATED_FRESHNESS_MILLIS)
    }

    internal fun areTopTracksFresh(entry: ArtistDetailsCacheEntry): Boolean {
        val updatedAt = entry.topTracksUpdatedAt ?: return false
        return entry.topTracks != null &&
            isFresh(updatedAt, DetailCachePolicy.RELATED_FRESHNESS_MILLIS)
    }

    @Synchronized
    internal fun putArtist(
        key: ArtistDetailsCacheKey,
        artist: Artist,
        artworkRefreshed: Boolean
    ) {
        val cached = entries[key]
        val now = clock()
        entries.put(
            key,
            ArtistDetailsCacheEntry(
                artist = artist,
                artistUpdatedAt = now,
                artworkUpdatedAt = if (artworkRefreshed) now else cached?.artworkUpdatedAt,
                topAlbums = cached?.topAlbums,
                topAlbumsUpdatedAt = cached?.topAlbumsUpdatedAt,
                topTracks = cached?.topTracks,
                topTracksUpdatedAt = cached?.topTracksUpdatedAt
            )
        )
    }

    @Synchronized
    internal fun putTopAlbums(key: ArtistDetailsCacheKey, albums: List<TopAlbum>) {
        val cached = entries[key] ?: return
        entries.put(
            key,
            cached.copy(
                topAlbums = albums,
                topAlbumsUpdatedAt = clock()
            )
        )
    }

    @Synchronized
    internal fun updateArtwork(key: ArtistDetailsCacheKey, artist: Artist) {
        val cached = entries[key] ?: return
        entries.put(
            key,
            cached.copy(
                artist = artist,
                artworkUpdatedAt = clock()
            )
        )
    }

    @Synchronized
    internal fun putTopTracks(key: ArtistDetailsCacheKey, tracks: List<Track>) {
        val cached = entries[key] ?: return
        entries.put(
            key,
            cached.copy(
                topTracks = tracks,
                topTracksUpdatedAt = clock()
            )
        )
    }

    internal fun isFresh(updatedAt: Long, freshnessMillis: Long): Boolean {
        return isCacheValueFresh(clock(), updatedAt, freshnessMillis)
    }
}
