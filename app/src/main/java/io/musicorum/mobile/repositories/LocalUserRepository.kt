package io.musicorum.mobile.repositories

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import io.musicorum.mobile.datastore.LocalUser
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.models.PartialUser
import io.musicorum.mobile.serialization.User
import io.musicorum.mobile.serialization.UserData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.net.UnknownHostException
import java.util.Date

val Context.localUser: DataStore<Preferences> by preferencesDataStore("partialUser")

class LocalUserRepository(val context: Context) {
    private val userFlow = context.localUser.data.map {
        PartialUser(
            it[LocalUser.USERNAME_KEY] ?: "",
            it[LocalUser.PROFILE_ICON_KEY] ?: "",
            it[LocalUser.EXPIRES_IN_KEY] ?: 0L
        )
    }

    suspend fun getUser(): PartialUser {
        val user = userFlow.first()
        return if (Date().time > user.expiresIn) {
            Log.d("user repository", "fetching online")
            val fetchedUser = fetch()
            if (fetchedUser != null) {
                val updatedUser = PartialUser(
                    fetchedUser.user.name,
                    fetchedUser.user.bestImageUrl,
                    cacheTime
                )
                updateUser(updatedUser)
                return updatedUser
            } else {
                return user
            }
        } else {
            Log.d("user repository", "skipping fetch")
            user
        }
    }

    suspend fun create(baseUser: UserData?) {
        val partial = PartialUser(
            baseUser?.name ?: "",
            baseUser?.bestImageUrl ?: "",
            cacheTime
        )
        updateUser(partial)
    }

    suspend fun fetch(): User? {
        val user = userFlow.first()
        if (user.username.isEmpty()) return null
        return try {
            UserEndpoint.getUser(user.username)
        } catch(e: UnknownHostException) {
            null
        }
    }

    suspend fun updateUser(partialUser: PartialUser) {
        context.localUser.edit {
            it[LocalUser.USERNAME_KEY] = partialUser.username
            it[LocalUser.PROFILE_ICON_KEY] = partialUser.imageUrl
            it[LocalUser.EXPIRES_IN_KEY] = partialUser.expiresIn
        }
    }

    private val cacheTime = Date(Date().time + (1000 * 60 * 60 * 48)).time
}