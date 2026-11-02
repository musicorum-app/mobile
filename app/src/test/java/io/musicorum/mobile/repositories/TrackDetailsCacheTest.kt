package io.musicorum.mobile.repositories

import io.musicorum.mobile.serialization.entities.Artist
import io.musicorum.mobile.serialization.entities.Track
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class TrackDetailsCacheTest {
    @Test
    fun cacheKeyNormalizesValuesAndKeepsUsersSeparate() {
        val first = TrackDetailsCacheKey.create(" User ", "P.A.V", "Not Alone")
        val same = TrackDetailsCacheKey.create("user", "p.a.v", "not alone")
        val differentUser = TrackDetailsCacheKey.create("another", "p.a.v", "not alone")

        assertEquals(first, same)
        assertFalse(first == differentUser)
    }

    @Test
    fun freshnessUsesConfiguredClock() {
        var now = 1_000L
        val cache = TrackDetailsCache(maxEntries = 1, clock = { now })

        assertTrue(cache.isFresh(updatedAt = 1_000L, freshnessMillis = 100L))

        now = 1_100L
        assertFalse(cache.isFresh(updatedAt = 1_000L, freshnessMillis = 100L))
    }

    @Test
    fun leastRecentlyUsedEntryIsEvicted() {
        val cache = TrackDetailsCache(maxEntries = 2, clock = { 0L })
        val first = key("first")
        val second = key("second")
        val third = key("third")

        cache.putTrack(
            first,
            track("first"),
            albumArtworkRefreshed = true,
            artistArtworkRefreshed = true
        )
        cache.putTrack(
            second,
            track("second"),
            albumArtworkRefreshed = true,
            artistArtworkRefreshed = true
        )
        cache.get(first)
        cache.putTrack(
            third,
            track("third"),
            albumArtworkRefreshed = true,
            artistArtworkRefreshed = true
        )

        assertNull(cache.get(second))
        assertEquals("first", cache.get(first)?.track?.name)
        assertEquals("third", cache.get(third)?.track?.name)
    }

    @Test
    fun trackUpdatePreservesFreshnessMetadata() {
        var now = 10L
        val cache = TrackDetailsCache(maxEntries = 1, clock = { now })
        val key = key("track")
        cache.putTrack(
            key,
            track("original"),
            albumArtworkRefreshed = true,
            artistArtworkRefreshed = true
        )
        val original = cache.get(key)!!

        now = 20L
        val updated = track("updated")
        cache.updateTrack(key, updated)
        val result = cache.get(key)!!

        assertSame(updated, result.track)
        assertEquals(original.trackUpdatedAt, result.trackUpdatedAt)
        assertEquals(original.albumArtworkUpdatedAt, result.albumArtworkUpdatedAt)
        assertEquals(original.artistArtworkUpdatedAt, result.artistArtworkUpdatedAt)
    }

    @Test
    fun artworkTimestampsAdvanceOnlyForSuccessfulRefreshes() {
        var now = 10L
        val cache = TrackDetailsCache(maxEntries = 1, clock = { now })
        val key = key("track")

        cache.putTrack(
            key,
            track("track"),
            albumArtworkRefreshed = true,
            artistArtworkRefreshed = false
        )
        val albumOnly = cache.get(key)!!
        assertEquals(10L, albumOnly.albumArtworkUpdatedAt)
        assertNull(albumOnly.artistArtworkUpdatedAt)

        now = 20L
        cache.putTrack(
            key,
            track("track"),
            albumArtworkRefreshed = false,
            artistArtworkRefreshed = true
        )
        val both = cache.get(key)!!
        assertEquals(10L, both.albumArtworkUpdatedAt)
        assertEquals(20L, both.artistArtworkUpdatedAt)
    }

    private fun key(track: String): TrackDetailsCacheKey {
        return TrackDetailsCacheKey.create("user", "artist", track)
    }

    private fun track(name: String): Track {
        return Track(
            artist = Artist(name = "artist"),
            name = name,
            url = ""
        )
    }
}
