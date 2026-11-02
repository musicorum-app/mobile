package io.musicorum.mobile.repositories

import android.os.SystemClock
import io.musicorum.mobile.ktor.endpoints.InnerAlbum
import javax.inject.Inject
import javax.inject.Singleton

internal data class AlbumDetailsCacheKey(
    val username: String,
    val artist: String,
    val album: String
) {
    companion object {
        fun create(username: String, artist: String, album: String): AlbumDetailsCacheKey {
            return AlbumDetailsCacheKey(
                username = username.normalizedCacheValue(),
                artist = artist.normalizedCacheValue(),
                album = album.normalizedCacheValue()
            )
        }
    }
}

internal data class AlbumDetailsCacheEntry(
    val album: InnerAlbum,
    val albumUpdatedAt: Long,
    val artistImageUrl: String? = null,
    val artistArtworkUpdatedAt: Long? = null
)

@Singleton
class AlbumDetailsCache internal constructor(
    maxEntries: Int,
    private val clock: () -> Long
) {
    @Inject
    constructor() : this(
        maxEntries = DetailCachePolicy.MAX_ENTRIES,
        clock = SystemClock::elapsedRealtime
    )

    private val entries = BoundedLruCache<AlbumDetailsCacheKey, AlbumDetailsCacheEntry>(maxEntries)

    internal fun get(key: AlbumDetailsCacheKey): AlbumDetailsCacheEntry? = entries[key]

    internal fun isAlbumFresh(entry: AlbumDetailsCacheEntry): Boolean {
        return isFresh(entry.albumUpdatedAt, DetailCachePolicy.DETAILS_FRESHNESS_MILLIS)
    }

    internal fun isArtistArtworkFresh(entry: AlbumDetailsCacheEntry): Boolean {
        val updatedAt = entry.artistArtworkUpdatedAt ?: return false
        return entry.artistImageUrl != null &&
            isFresh(updatedAt, DetailCachePolicy.ARTWORK_FRESHNESS_MILLIS)
    }

    @Synchronized
    internal fun putAlbum(key: AlbumDetailsCacheKey, album: InnerAlbum) {
        val cached = entries[key]
        entries.put(
            key,
            AlbumDetailsCacheEntry(
                album = album,
                albumUpdatedAt = clock(),
                artistImageUrl = cached?.artistImageUrl,
                artistArtworkUpdatedAt = cached?.artistArtworkUpdatedAt
            )
        )
    }

    @Synchronized
    internal fun putArtistImage(key: AlbumDetailsCacheKey, imageUrl: String) {
        val cached = entries[key] ?: return
        entries.put(
            key,
            cached.copy(
                artistImageUrl = imageUrl,
                artistArtworkUpdatedAt = clock()
            )
        )
    }

    internal fun isFresh(updatedAt: Long, freshnessMillis: Long): Boolean {
        return isCacheValueFresh(clock(), updatedAt, freshnessMillis)
    }
}
