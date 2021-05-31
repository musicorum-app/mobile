package com.musicorumapp.mobile.api.models

data class AuthSessionResponse(
    val session: AuthSession
) {
    data class AuthSession(
        var name: String,
        var key: String
    )
}
