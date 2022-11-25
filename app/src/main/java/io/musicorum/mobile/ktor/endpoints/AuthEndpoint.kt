package io.musicorum.mobile.ktor.endpoints

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.musicorum.mobile.ktor.KtorConfiguration
import io.musicorum.mobile.serialization.SessionResponse

object AuthEndpoint {
    suspend fun getSession(token: String): SessionResponse? {
        val res = KtorConfiguration.lastFmClient.get {
            parameter("method", "auth.getSession")
            parameter("token", token)
        }
        return if (res.status.isSuccess()) {
            res.body<SessionResponse>()
        } else {
            null
        }
    }
}