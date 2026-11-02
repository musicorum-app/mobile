package io.musicorum.mobile.repositories

import io.musicorum.mobile.ktor.endpoints.InnerAlbum
import io.musicorum.mobile.serialization.entities.Album
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class AlbumDetailsCacheTest {
    @Test
    fun cacheKeyNormalizesValuesAndKeepsUsersSeparate() {
        val first = AlbumDetailsCacheKey.create(" User ", "P.A.V", " Not Alone ")
        val same = AlbumDetailsCacheKey.create("user", "p.a.v", "not alone")
        val differentUser = AlbumDetailsCacheKey.create("another", "p.a.v", "not alone")

        assertEquals(first, same)
        assertFalse(first == differentUser)
    }

    @Test
    fun albumRefreshPreservesCachedArtistArtwork() {
        var now = 10L
        val cache = AlbumDetailsCache(maxEntries = 1, clock = { now })
        val key = AlbumDetailsCacheKey.create("user", "artist", "album")
        cache.putAlbum(key, innerAlbum("first"))
        cache.putArtistImage(key, "image")

        now = 20L
        val refreshed = innerAlbum("refreshed")
        cache.putAlbum(key, refreshed)
        val result = cache.get(key)!!

        assertSame(refreshed, result.album)
        assertEquals(20L, result.albumUpdatedAt)
        assertEquals("image", result.artistImageUrl)
        assertEquals(10L, result.artistArtworkUpdatedAt)
    }

    @Test
    fun artistArtworkIsNotStoredWithoutAnAlbumEntry() {
        val cache = AlbumDetailsCache(maxEntries = 1, clock = { 0L })
        val key = AlbumDetailsCacheKey.create("user", "artist", "album")

        cache.putArtistImage(key, "image")

        assertNull(cache.get(key))
    }

    private fun innerAlbum(name: String): InnerAlbum {
        return InnerAlbum(Album(name = name, artist = "artist"))
    }
}
