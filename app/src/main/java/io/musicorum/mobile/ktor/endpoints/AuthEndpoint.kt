package io.musicorum.mobile.ktor.endpoints

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.musicorum.mobile.ktor.KtorConfiguration
import io.musicorum.mobile.serialization.SessionResponse

class AuthEndpoint {
    suspend fun getSession(token: String): SessionResponse {
        val session: SessionResponse = KtorConfiguration.lastFmClient.get {
            parameter("method", "auth.getSession")
            parameter("token", token)
        }.body()

        return session
    }
}