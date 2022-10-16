package io.musicorum.mobile.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val user: UserData
)


@Serializable
data class UserData(
    val name: String,
    val image: List<Image>,
    @SerialName("playcount")
    val scrobbles: String
)

