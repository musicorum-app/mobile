package io.musicorum.mobile.serialization

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class SearchResponse(
    val results: JsonObject
)
