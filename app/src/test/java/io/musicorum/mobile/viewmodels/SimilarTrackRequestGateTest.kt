package io.musicorum.mobile.viewmodels

import io.musicorum.mobile.repositories.TrackDetailsCacheKey
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SimilarTrackRequestGateTest {
    @Test
    fun duplicateRequestIsRejectedUntilActiveRequestFinishes() {
        val gate = SimilarTrackRequestGate()
        val key = key("track")

        assertTrue(gate.tryStart(key))
        assertFalse(gate.tryStart(key))

        gate.finish(key)
        assertTrue(gate.tryStart(key))
    }

    @Test
    fun finishingOldRequestDoesNotReleaseNewRequest() {
        val gate = SimilarTrackRequestGate()
        val oldKey = key("old")
        val newKey = key("new")

        assertTrue(gate.tryStart(oldKey))
        assertTrue(gate.tryStart(newKey))

        gate.finish(oldKey)
        assertFalse(gate.tryStart(newKey))

        gate.finish(newKey)
        assertTrue(gate.tryStart(newKey))
    }

    private fun key(track: String): TrackDetailsCacheKey {
        return TrackDetailsCacheKey.create("user", "artist", track)
    }
}
