package io.musicorum.mobile.ktor.endpoints

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.musicorum.mobile.ktor.KtorConfiguration
import io.musicorum.mobile.serialization.User

class UserEndpoint {
/*    suspend fun getInfo(username: String): User? {
        val fetched = KtorConfiguration.lastFmClient.get {
            parameter("method", "user.getInfo")
            parameter("user", username)
        }

        return if (fetched.status == HttpStatusCode.OK) {
            fetched.body<User>()
        } else {
            null
        }
    }*/

    suspend fun getInfo(sessionKey: String): User? {
        val fetched = KtorConfiguration.lastFmClient.get {
            parameter("method", "user.getInfo")
            parameter("sk", sessionKey)
        }

        return if (fetched.status == HttpStatusCode.OK) {
            fetched.body<User>()
        } else {
            null
        }
    }
}