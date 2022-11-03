package io.musicorum.mobile.ktor.endpoints

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.musicorum.mobile.ktor.KtorConfiguration
import io.musicorum.mobile.serialization.BaseIndividualTrack
import io.musicorum.mobile.serialization.SimilarTrack
import io.musicorum.mobile.serialization.Track

class TrackEndpoint {
    suspend fun getTrack(
        trackName: String,
        artist: String,
        username: String?,
        autoCorrect: Boolean?
    ): BaseIndividualTrack {
        val res: BaseIndividualTrack = KtorConfiguration.lastFmClient.get {
            val autoCorrectValue = if (autoCorrect == true) 1 else 0
            parameter("track", trackName)
            parameter("method", "track.getInfo")
            parameter("username", username)
            parameter("artist", artist)
            parameter("autocorrect", autoCorrectValue)
        }.body()
        return res
    }

    suspend fun updateFavoritePreference(track: Track, favorite: Boolean, sessionKey: String) {
        KtorConfiguration.lastFmClient.post {
            parameter("method", if (favorite) "track.love" else "track.unlove")
            parameter("track", track.name)
            parameter("artist", track.artist.artistName)
            parameter("sk", sessionKey)
        }
    }

    suspend fun fetchSimilar(baseTrack: Track, limit: Int?, autoCorrect: Boolean?): SimilarTrack? {
        val autoCorrectValue = if (autoCorrect == true) "1" else "0"
        val res = KtorConfiguration.lastFmClient.get {
            parameter("method", "track.getSimilar")
            parameter("artist", baseTrack.artist.artistName)
            parameter("track", baseTrack.name)
            parameter("limit", limit)
            parameter("autocorrect", autoCorrectValue)
        }
        return if (res.status == HttpStatusCode.OK) {
            res.body<SimilarTrack>()
        } else {
            null
        }
    }

}
