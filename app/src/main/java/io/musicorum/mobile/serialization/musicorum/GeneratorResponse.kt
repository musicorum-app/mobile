package io.musicorum.mobile.serialization.musicorum

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeneratorResponse(
    val duration: Float,
    val file: String,
    val id: String,
    val url: String,
    @SerialName("trace_id")
    val traceId: String
)
