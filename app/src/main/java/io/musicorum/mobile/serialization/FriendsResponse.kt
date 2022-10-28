package io.musicorum.mobile.serialization

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class FriendsResponse(
    val friends: InnerFriendsResponse
)

@kotlinx.serialization.Serializable
data class InnerFriendsResponse(
    @SerialName("user")
    val users: List<UserData>
)
