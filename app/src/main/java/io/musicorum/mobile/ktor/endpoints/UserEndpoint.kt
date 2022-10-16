package io.musicorum.mobile.ktor.endpoints

import android.content.SharedPreferences
import io.musicorum.mobile.ktor.KtorConfiguration
import io.musicorum.mobile.serialization.User
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class UserEndpoint {
    suspend fun getInfo(username: String): User? {
        val fetched = KtorConfiguration.lastFmClient.get {
            parameter("method", "user.getInfo")
            parameter("user", username)
        }

        return if (fetched.status == HttpStatusCode.OK) {
            fetched.body<User>()
        } else {
            null
        }
    }
    suspend fun getInfo(sharedPreferences: SharedPreferences): User? {
        val fetched = KtorConfiguration.lastFmClient.get {
            parameter("method", "user.getInfo")
            parameter("sk", sharedPreferences.getString("l_key", null))
        }

        return if (fetched.status == HttpStatusCode.OK) {
            fetched.body<User>()
        } else {
            null
        }
    }
}