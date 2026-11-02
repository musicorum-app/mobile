package io.musicorum.mobile.ktor.endpoints

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TrackEndpointTest {
    @Test
    fun errorResponseReturnsNull() {
        val response = """{"error":6,"message":"Track not found"}"""

        assertNull(decodeTrackResponse(response))
    }

    @Test
    fun trackResponseReturnsTrack() {
        val response = """
            {
                "track": {
                    "artist": {"name": "Artist"},
                    "name": "Song",
                    "url": "https://www.last.fm/music/Artist/_/Song"
                }
            }
        """.trimIndent()

        assertEquals("Song", decodeTrackResponse(response)?.track?.name)
    }
}
