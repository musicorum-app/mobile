package io.musicorum.mobile.ktor

import android.util.Log
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.musicorum.mobile.BuildConfig
import io.musicorum.mobile.utils.md5Hash
import kotlinx.serialization.json.Json

object KtorConfiguration {
    private val jsonConfig = Json { ignoreUnknownKeys = true; isLenient = true }
    private fun createLastFmClient(): HttpClient {
        val lastFmClient = HttpClient {
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
            }

            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "ws.audioscrobbler.com"
                    path("2.0/")
                    parameters.append("api_key", BuildConfig.LASTFM_API_KEY)
                    parameters.append("format", "json")
                    header("Cache-Control", "max-age=3600, public")
                }
            }
        }
        lastFmClient.plugin(HttpSend).intercept { req ->
            var signature = ""
            req.url.parameters.names().sorted().forEach { param ->
                if (param != "format") {
                    signature += param + req.url.parameters[param]
                }
            }
            signature += BuildConfig.LASTFM_SECRET
            val md5 = md5Hash(signature)
            req.parameter("api_sig", md5)
            execute(req)
        }
        return lastFmClient
    }

    val musicorumClient = HttpClient {
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
                parameters.append("sources", "spotify,deezer")
                parameters.append("api_key", BuildConfig.MUSICORUM_API_KEY)
                header("Cache-Control", "max-age=3600, public")
            }
        }
    }

    val lastFmClient = createLastFmClient()
}
