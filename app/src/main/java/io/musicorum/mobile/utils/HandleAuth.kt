package io.musicorum.mobile.utils

import android.content.Context
import io.musicorum.mobile.ktor.endpoints.AuthEndpoint
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.models.PartialUser
import io.musicorum.mobile.repositories.LocalUserRepository
import io.musicorum.mobile.serialization.User
import java.util.Date

suspend fun handleAuth(
    token: String,
    ctx: Context,
    userBlock: (user: User?, sessionKey: String) -> Unit
) {
    val cacheTime = Date(Date().time + (1000 * 60)).time
    val sessionKey = AuthEndpoint.getSession(token)?.session?.key
    sessionKey?.let { s ->
        val user = UserEndpoint.getSessionUser(s)
        user?.let {
            LocalUserRepository(ctx).updateUser(
                PartialUser(
                    it.user.name,
                    it.user.bestImageUrl,
                    cacheTime
                )
            )
        }
        return userBlock(user, s)
    }
}
