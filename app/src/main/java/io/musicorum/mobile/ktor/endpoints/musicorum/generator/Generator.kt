package io.musicorum.mobile.ktor.endpoints.musicorum.generator

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.musicorum.mobile.BuildConfig
import io.musicorum.mobile.ktor.endpoints.musicorum.generator.models.BaseBody
import io.musicorum.mobile.ktor.endpoints.musicorum.generator.models.DuotoneOptions
import io.musicorum.mobile.ktor.endpoints.musicorum.generator.models.GridOptions
import io.musicorum.mobile.serialization.musicorum.GeneratorResponse
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

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

    private val module = SerializersModule {
        polymorphic(IThemeOptions::class) {
            subclass(GridOptions::class)
            subclass(DuotoneOptions::class)
        }
    }

    val json = Json {
        serializersModule = module
        encodeDefaults = true
    }

    internal object ThemeSerializer :
        JsonContentPolymorphicSerializer<IThemeOptions>(IThemeOptions::class) {
        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<IThemeOptions> {
            return if (element.jsonObject["rows"] != null) {
                GridOptions.serializer()
            } else {
                GridOptions.serializer()
            }
        }
    }

    /**
     * Generates a grid collage
     * @param username The last.fm usernameArg
     * @param rowCount Row count
     * @param colCount Column count
     * @param entity Entity, either artist, album or track
     * @param period Period, use built-in parsers to convert to string
     * @param showNames Whether it will show entity names on the collage
     * @return A string pointing to the image URL or null if error
     */
    suspend fun generateGrid(
        username: String,
        rowCount: Int,
        colCount: Int,
        entity: String,
        period: String,
        showNames: Boolean,
    ): String? {

        val options: IThemeOptions = GridOptions(
            rows = rowCount,
            columns = colCount,
            showNames = showNames,
            period = period,
            entity = entity,
            style = "DEFAULT",
            showPlayCount = true
        )

        val generatorBody = BaseBody(
            user = username,
            theme = "grid",
            language = "en-US",
            options = options,
            story = true,
            hideUsername = true
        )

        val res = client.post {
            url("/collages/generate")
            setBody(json.encodeToString(generatorBody))
            contentType(ContentType.Application.Json)
        }

        if (!res.status.isSuccess()) {
            Log.e(
                "Generator",
                "HTTP ${res.status.value} | ${res.status.description} \n ${res.bodyAsText()}"
            )
            throw Exception("Couldn't generate grid collage (${res.status.value} ${res.status.description})")
        }

        return if (res.status.isSuccess()) {
            res.body<GeneratorResponse>().url
        } else null
    }

    suspend fun generateDuotone(
        username: String,
        entity: String,
        period: String,
        palette: String,
        story: Boolean,
        hideUsername: Boolean
    ): String? {
        val options: IThemeOptions = DuotoneOptions(
            palette = palette.uppercase(),
            entity = entity,
            period = period.uppercase(),
        )
        val body = BaseBody(
            language = "en-US",
            hideUsername = hideUsername,
            options = options,
            story = story,
            theme = "duotone",
            user = username
        )

        val res = client.post {
            url("/collages/generate")
            setBody(json.encodeToString(body))
            contentType(ContentType.Application.Json)
        }

        if (!res.status.isSuccess()) {
            Log.e(
                "Generator",
                "HTTP ${res.status.value} | ${res.status.description} \n ${res.bodyAsText()}"
            )
            throw Exception("Couldn't generate duotone collage (${res.status.value} ${res.status.description})")
        }

        return if (res.status.isSuccess()) {
            res.body<GeneratorResponse>().url
        } else null
    }
}
