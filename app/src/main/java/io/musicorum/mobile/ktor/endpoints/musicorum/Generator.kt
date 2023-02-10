package io.musicorum.mobile.ktor.endpoints.musicorum

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.musicorum.mobile.BuildConfig
import io.musicorum.mobile.serialization.musicorum.GeneratorResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object Generator {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { encodeDefaults = true; isLenient = true })
        }
        defaultRequest {
            url {
                host = "api-v2.musicorumapp.com"
                header("Cache-Control", "max-age=3600")
                parameters.append("api_key", BuildConfig.MUSICORUM_API_KEY)
                protocol = URLProtocol.HTTPS
            }
        }
    }

    suspend fun generateGrid(
        username: String,
        rowCount: Int,
        colCount: Int,
        entity: String,
        period: String,
        showNames: Boolean,
    ): String? {
        val options = GeneratorOptions(
            rows = rowCount,
            columns = colCount,
            showNames = showNames,
            period = period,
            entity = entity,
            style = "DEFAULT",
            showPlaycount = true
        )
        val generatorBody = GeneratorBody(
            user = username,
            theme = "grid",
            language = "en-US",
            options = options,
            story = true,
            hideUsername = true
        )

        val res = client.post {
            url("/collages/generate")
            setBody(Json.encodeToString(generatorBody))
            contentType(ContentType.Application.Json)
        }

        Log.d("chart", generatorBody.toString())

        return if (res.status.isSuccess()) {
            res.body<GeneratorResponse>().url
        } else null
    }
}

@Serializable
private data class GeneratorBody(
    val user: String,
    val theme: String,
    val language: String,
    val options: GeneratorOptions,
    val story: Boolean = false,
    @SerialName("hide_username")
    val hideUsername: Boolean = false
)

@Serializable
private data class GeneratorOptions(
    @SerialName("show_playcount")
    val showPlaycount: Boolean = true,
    val rows: Int?,
    val columns: Int?,
    @SerialName("show_names")
    val showNames: Boolean,
    val period: String,
    val entity: String,
    val style: String,
)
