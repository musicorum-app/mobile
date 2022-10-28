package io.musicorum.mobile.ktor.endpoints

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.musicorum.mobile.ktor.KtorConfiguration
import io.musicorum.mobile.serialization.BaseIndividualTrack
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
            parameter("artist", track.artist.name)
            parameter("sk", sessionKey)
        }
    }

    @kotlinx.serialization.Serializable
    private data class RequestBody(
        val trackName: String,
        val artist: String,
        val api_key: String,
        val api_sig: String,
        val sk: String
    )
}
