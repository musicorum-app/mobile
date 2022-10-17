package io.musicorum.mobile.ktor.endpoints

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.musicorum.mobile.ktor.KtorConfiguration
import io.musicorum.mobile.serialization.TopTracks

enum class FetchPeriod(period: String) {
    WEEK("7day"),
    MONTH("1month"),
    TRIMESTER("3month"),
    SEMESTER("6month"),
    YEAR("12month");

    val value = period
}


class TopTracksEndpoint {
    suspend fun fetchTopTracks(user: String, period: FetchPeriod?, limit: Int?): TopTracks {
        val res: TopTracks = KtorConfiguration.lastFmClient.get {
            parameter("method", "user.getTopTracks")
            parameter("user", user)
            parameter("period", period?.value)
            parameter("limit", limit)
        }.body()
        return res
    }
}