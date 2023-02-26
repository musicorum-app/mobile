package io.musicorum.mobile.ktor

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLProtocol
import io.ktor.http.isSuccess
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import io.musicorum.mobile.BuildConfig
import io.musicorum.mobile.utils.CustomCacheControl
import io.musicorum.mobile.utils.md5Hash
import io.sentry.Sentry
import kotlinx.serialization.json.Json

object KtorConfiguration {
    private val jsonConfig = Json { ignoreUnknownKeys = true; isLenient = true }
    private val KEY_REQUIRED_METHODS =
        listOf(
            "user.getInfo",
            "track.love",
            "track.unlove",
            "auth.getSession",
            "track.scrobble",
            "track.updateNowPlaying"
        )

    private fun createLastFmClient(): HttpClient {
        val lastFmClient = HttpClient {
            HttpResponseValidator {
                validateResponse { res ->
                    if (res.status.value >= 500) {
                        Sentry.captureException(ServerResponseException(res, res.bodyAsText()))
                    } else {
                        Sentry.captureException(ClientRequestException(res, res.bodyAsText()))
                    }
                }
            }
            install(ContentNegotiation) {
                json(jsonConfig)
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("LAST.FM", message)
                    }
                }
                level = LogLevel.INFO
            }
            install(HttpCache) {
                publicStorage(CustomCacheControl)
            }

            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "ws.audioscrobbler.com"
                    path("2.0/")
                    parameters.append("api_key", BuildConfig.LASTFM_API_KEY)
                    parameters.append("format", "json")
                    headers {
                        append("Cache-Control", "max-age=3600, public")
                    }
                }
            }
        }
        lastFmClient.plugin(HttpSend).intercept { req ->
            if (req.url.parameters["method"] in KEY_REQUIRED_METHODS) {
                var signature = ""
                req.url.parameters.names().sorted().forEach { param ->
                    if (param != "format") {
                        signature += param + req.url.parameters[param]
                    }
                }
                signature += BuildConfig.LASTFM_SECRET
                val md5 = md5Hash(signature)
                req.parameter("api_sig", md5)
            }
            execute(req)
        }
        return lastFmClient
    }

    val musicorumClient = HttpClient {
        HttpResponseValidator {
            validateResponse { res ->
                if (!res.status.isSuccess()) {
                    if (res.status.value >= 500) {
                        Sentry.captureException(ServerResponseException(res, res.bodyAsText()))
                    } else {
                        Sentry.captureException(ClientRequestException(res, res.bodyAsText()))
                    }
                }
            }
        }
        expectSuccess = true
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("MUSICORUM", message)
                }
            }
            level = LogLevel.ALL
        }

        install(HttpCache)

        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = "api-v2.musicorumapp.com"
                path("/v2/resources")
                parameters.append("sources", "spotify,deezer,lastfm")
                parameters.append("api_key", BuildConfig.MUSICORUM_API_KEY)
                header("Cache-Control", "max-age=3600")
            }
        }
    }

    val lastFmClient = createLastFmClient()
}
