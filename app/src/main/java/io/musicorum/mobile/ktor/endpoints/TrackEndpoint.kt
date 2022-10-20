package io.musicorum.mobile.ktor.endpoints

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.musicorum.mobile.ktor.KtorConfiguration
import io.musicorum.mobile.serialization.BaseIndividualTrack

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
}