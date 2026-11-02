package io.musicorum.mobile.repositories

import io.musicorum.mobile.serialization.TopAlbum
import io.musicorum.mobile.serialization.entities.Artist
import io.musicorum.mobile.serialization.entities.Track
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ArtistDetailsCacheTest {
    @Test
    fun cacheKeyNormalizesValuesAndKeepsUsersSeparate() {
        val first = ArtistDetailsCacheKey.create(" User ", "P.A.V")
        val same = ArtistDetailsCacheKey.create("user", "p.a.v")
        val differentUser = ArtistDetailsCacheKey.create("another", "p.a.v")

        assertEquals(first, same)
        assertFalse(first == differentUser)
    }

    @Test
    fun relatedDataRefreshPreservesArtistAndArtworkMetadata() {
        var now = 10L
        val cache = ArtistDetailsCache(maxEntries = 1, clock = { now })
        val key = ArtistDetailsCacheKey.create("user", "artist")
        val artist = Artist(name = "artist")
        cache.putArtist(key, artist, artworkRefreshed = true)

        now = 20L
        val albums = listOf(TopAlbum(name = "album"))
        val tracks = listOf(Track(artist = artist, name = "track", url = ""))
        cache.putTopAlbums(key, albums)
        cache.putTopTracks(key, tracks)
        val result = cache.get(key)!!

        assertSame(artist, result.artist)
        assertEquals(10L, result.artistUpdatedAt)
        assertEquals(10L, result.artworkUpdatedAt)
        assertSame(albums, result.topAlbums)
        assertSame(tracks, result.topTracks)
        assertEquals(20L, result.topAlbumsUpdatedAt)
        assertEquals(20L, result.topTracksUpdatedAt)
    }

    @Test
    fun artworkTimestampAdvancesOnlyAfterSuccessfulRefresh() {
        var now = 10L
        val cache = ArtistDetailsCache(maxEntries = 1, clock = { now })
        val key = ArtistDetailsCacheKey.create("user", "artist")
        cache.putArtist(key, Artist(name = "artist"), artworkRefreshed = false)
        assertNull(cache.get(key)?.artworkUpdatedAt)

        now = 20L
        cache.updateArtwork(key, Artist(name = "artist"))

        assertEquals(20L, cache.get(key)?.artworkUpdatedAt)
    }

    @Test
    fun freshnessUsesConfiguredClock() {
        var now = 1_000L
        val cache = ArtistDetailsCache(maxEntries = 1, clock = { now })

        assertTrue(cache.isFresh(updatedAt = now, freshnessMillis = 100L))

        now = 1_100L
        assertFalse(cache.isFresh(updatedAt = 1_000L, freshnessMillis = 100L))
    }
}
