package io.musicorum.mobile.ktor.endpoints

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.musicorum.mobile.ktor.KtorConfiguration
import io.musicorum.mobile.models.FetchPeriod
import io.musicorum.mobile.serialization.FriendsResponse
import io.musicorum.mobile.serialization.RecentTracks
import io.musicorum.mobile.serialization.TopAlbumsResponse
import io.musicorum.mobile.serialization.TopArtistsResponse
import io.musicorum.mobile.serialization.User
import io.musicorum.mobile.serialization.entities.TopTracks

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
        return kotlin.runCatching {
            val fetched = KtorConfiguration.lastFmClient.get {
                parameter("method", "user.getInfo")
                parameter("sk", sessionKey)
            }

            return@runCatching if (fetched.status.isSuccess()) {
                fetched.body<User>()
            } else {
                null
            }
        }.getOrNull()
    }

    suspend fun getTopArtists(
        username: String,
        limit: Int?,
        period: FetchPeriod?
    ): TopArtistsResponse? {
        return kotlin.runCatching {
            val res = KtorConfiguration.lastFmClient.get {
                parameter("method", "user.getTopArtists")
                parameter("user", username)
                parameter("limit", limit)
                parameter("period", period?.value)
            }
            return@runCatching if (res.status.isSuccess()) {
                return res.body<TopArtistsResponse>()
            } else {
                null
            }
        }.getOrNull()
    }

    suspend fun getRecentTracks(
        user: String,
        from: String? = null,
        limit: Int? = null,
        extended: Boolean? = false,
        page: Int? = null
    ): RecentTracks? {
        val extendedValue = if (extended == true) "1" else "0"
        val res = KtorConfiguration.lastFmClient.get {
            headers.remove("Cache-Control")
            parameter("method", "user.getRecentTracks")
            parameter("user", user)
            parameter("from", from)
            parameter("limit", limit)
            parameter("extended", extendedValue)
            parameter("page", page)
        }
        return if (res.status.isSuccess()) {
            res.body<RecentTracks>()
        } else null

    }

    suspend fun getFriends(user: String, limit: Int?): FriendsResponse?  {
        val result = kotlin.runCatching {
            val res = KtorConfiguration.lastFmClient.get {
                parameter("method", "user.getFriends")
                parameter("username", user)
                parameter("limit", limit)
                headers.remove("Cache-Control")
            }
            return@runCatching if (res.status.isSuccess()) {
                res.body<FriendsResponse>()
            } else null
        }

        return result.getOrNull()
    }

    suspend fun getTopTracks(user: String, period: FetchPeriod?, limit: Int?): TopTracks? {
        return kotlin.runCatching {
            val res = KtorConfiguration.lastFmClient.get {
                parameter("method", "user.getTopTracks")
                parameter("user", user)
                parameter("period", period?.value)
                parameter("limit", limit)
            }
            return@runCatching if (res.status.isSuccess()) {
                res.body<TopTracks>()
            } else {
                null
            }
        }.getOrNull()
    }

    suspend fun getTopAlbums(user: String, period: FetchPeriod?, limit: Int?): TopAlbumsResponse? {
        return kotlin.runCatching {
            val res = KtorConfiguration.lastFmClient.get {
                parameter("method", "user.getTopAlbums")
                parameter("user", user)
                parameter("period", period?.value)
                parameter("limit", limit)
            }

            return@runCatching if (res.status.isSuccess()) {
                res.body<TopAlbumsResponse>()
            } else {
                null
            }
        }.getOrNull()
    }

    suspend fun updateNowPlaying(
        track: String,
        artist: String,
        album: String?,
        albumArtist: String?,
        sessionKey: String
    ): Boolean {
        val req = KtorConfiguration.lastFmClient.post {
            parameter("artist", artist)
            parameter("track", track)
            parameter("album", album)
            parameter("albumArtist", albumArtist)
            parameter("sk", sessionKey)
            parameter("method", "track.updateNowPlaying")
        }
        return req.status.isSuccess()
    }

    suspend fun scrobble(
        track: String,
        artist: String,
        album: String? = null,
        albumArtist: String? = null,
        sessionKey: String,
        timestamp: Long
    ): HttpStatusCode {
        val req = KtorConfiguration.lastFmClient.post {
            parameter("artist", artist)
            parameter("track", track)
            parameter("album", album)
            parameter("albumArtist", albumArtist)
            parameter("sk", sessionKey)
            parameter("method", "track.scrobble")
            parameter("timestamp", timestamp)
        }
        return req.status
    }
}