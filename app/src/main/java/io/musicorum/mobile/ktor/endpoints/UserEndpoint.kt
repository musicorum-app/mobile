package io.musicorum.mobile.ktor.endpoints

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.musicorum.mobile.ktor.KtorConfiguration
import io.musicorum.mobile.serialization.*

object UserEndpoint {
    suspend fun getUser(username: String): User? {
        val fetched = KtorConfiguration.lastFmClient.get {
            parameter("method", "user.getInfo")
            parameter("user", username)
        }

        return if (fetched.status.isSuccess()) {
            fetched.body<User>()
        } else {
            null
        }
    }

    suspend fun getSessionUser(sessionKey: String): User? {
        val fetched = KtorConfiguration.lastFmClient.get {
            parameter("method", "user.getInfo")
            parameter("sk", sessionKey)
        }

        return if (fetched.status.isSuccess()) {
            fetched.body<User>()
        } else {
            null
        }
    }

    suspend fun getTopArtists(
        username: String,
        limit: Int?,
        period: FetchPeriod?
    ): TopArtistsResponse? {
        val res = KtorConfiguration.lastFmClient.get {
            parameter("method", "user.getTopArtists")
            parameter("user", username)
            parameter("limit", limit)
            parameter("period", period?.value)
        }
        return if (res.status.isSuccess()) {
            return res.body<TopArtistsResponse>()
        } else {
            null
        }
    }

    suspend fun getRecentTracks(
        user: String,
        from: String?,
        limit: Int?,
        extended: Boolean?
    ): RecentTracks? {
        val extendedValue = if (extended == true) "1" else "0"
        val res = KtorConfiguration.lastFmClient.get {
            parameter("method", "user.getRecentTracks")
            parameter("user", user)
            parameter("from", from)
            parameter("limit", limit)
            parameter("extended", extendedValue)
        }
        return if (res.status.isSuccess()) {
            res.body<RecentTracks>()
        } else {
            null
        }
    }

    suspend fun getFriends(user: String, limit: Int?): FriendsResponse? {
        val res = KtorConfiguration.lastFmClient.get {
            parameter("method", "user.getFriends")
            parameter("username", user)
            parameter("limit", limit)
        }
        return if (res.status.isSuccess()) {
            res.body<FriendsResponse>()
        } else null
    }

    suspend fun getTopTracks(user: String, period: FetchPeriod?, limit: Int?): TopTracks? {
        val res = KtorConfiguration.lastFmClient.get {
            parameter("method", "user.getTopTracks")
            parameter("user", user)
            parameter("period", period?.value)
            parameter("limit", limit)
        }
        return if (res.status.isSuccess()) {
            res.body<TopTracks>()
        } else {
            null
        }
    }

    suspend fun getTopAlbums(user: String, period: FetchPeriod?, limit: Int?): TopAlbumsResponse? {
        val res = KtorConfiguration.lastFmClient.get {
            parameter("method", "user.getTopAlbums")
            parameter("user", user)
            parameter("period", period?.value)
            parameter("limit", limit)
        }

        return if (res.status.isSuccess()) {
            return res.body<TopAlbumsResponse>()
        } else {
            null
        }
    }
}