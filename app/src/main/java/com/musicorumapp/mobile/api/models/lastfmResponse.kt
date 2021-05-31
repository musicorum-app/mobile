package com.musicorumapp.mobile.api.models


// TODO: Implement this
data class ErrorResponse(
    val error: Int,
    val message: String
)

// TODO: Implement this
data class LastfmResponse<T>(
    val error: ErrorResponse?,
    val data: T?
)