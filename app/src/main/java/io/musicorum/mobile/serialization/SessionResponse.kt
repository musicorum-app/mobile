package io.musicorum.mobile.serialization

import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val name: String,
    val key: String,
)

@Serializable
data class SessionResponse(
    val session: Session
)
