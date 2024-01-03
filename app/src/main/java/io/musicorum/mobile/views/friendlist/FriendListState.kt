package io.musicorum.mobile.views.friendlist

import io.musicorum.mobile.serialization.UserData

data class FriendListState(
    val friends: List<UserData> = emptyList(),
    val loading: Boolean = false,
    val pinnedUsers: Set<String> = emptySet()
)