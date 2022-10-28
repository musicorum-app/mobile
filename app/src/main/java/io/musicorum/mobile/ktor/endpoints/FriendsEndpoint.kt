package io.musicorum.mobile.ktor.endpoints

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.musicorum.mobile.ktor.KtorConfiguration
import io.musicorum.mobile.serialization.FriendsResponse

class FriendsEndpoint {
    suspend fun fetchFriends(user: String, limit: Int?): FriendsResponse {
        val res: FriendsResponse = KtorConfiguration.lastFmClient.get {
            parameter("method", "user.getFriends")
            parameter("username", user)
            parameter("limit", limit)
        }.body()
        return res
    }
}