package io.musicorum.mobile.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendsResponse(
    val friends: InnerFriendsResponse
)

@Serializable
data class InnerFriendsResponse(
    @SerialName("user")
    val users: List<UserData>
)
