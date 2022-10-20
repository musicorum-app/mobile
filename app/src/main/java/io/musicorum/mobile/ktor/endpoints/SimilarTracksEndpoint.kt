package io.musicorum.mobile.ktor.endpoints

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.musicorum.mobile.ktor.KtorConfiguration
import io.musicorum.mobile.serialization.SimilarTrack
import io.musicorum.mobile.serialization.Track

class SimilarTracksEndpoint {
    suspend fun fetchSimilar(baseTrack: Track, limit: Int?, autoCorrect: Boolean?): SimilarTrack {
        val autoCorrectValue = if (autoCorrect == true) "1" else "0"
        val res: SimilarTrack = KtorConfiguration.lastFmClient.get {
            parameter("method", "track.getSimilar")
            parameter("artist", baseTrack.artist.name)
            parameter("track", baseTrack.name)
            parameter("autocorrect", autoCorrectValue)
        }.body()
        return res
    }
}