package io.musicorum.mobile.ktor.endpoints

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.musicorum.mobile.ktor.KtorConfiguration
import io.musicorum.mobile.serialization.RecentTracks

class RecentTracksEndpoint {
    suspend fun getRecentTracks(user: String, from: String?, limit: Int?): RecentTracks {
        val res: RecentTracks = KtorConfiguration.lastFmClient.get {
            parameter("method", "user.getRecentTracks")
            parameter("user", user)
            parameter("from", from)
            parameter("limit", limit)
        }.body()
        return res
    }
}