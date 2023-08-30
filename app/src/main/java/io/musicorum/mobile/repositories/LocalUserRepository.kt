package io.musicorum.mobile.repositories

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.models.PartialUser
import io.musicorum.mobile.serialization.User
import io.musicorum.mobile.serialization.UserData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Date

val Context.localUser: DataStore<Preferences> by preferencesDataStore("partialUser")

class LocalUserRepository(val context: Context) {
    val prefUser = context.localUser.data.map {
        PartialUser(
            it[usernameKey] ?: "",
            it[pfpKey] ?: "",
            it[expiresKey] ?: 0L
        )
    }

    suspend fun getUser(): PartialUser {
        val user = prefUser.first()
        return if (Date().time > user.expiresIn) {
            Log.d("user repository", "fetching online")
            val fetchedUser = fetch()
            val updatedUser = PartialUser(
                fetchedUser?.user?.name ?: "",
                fetchedUser?.user?.bestImageUrl ?: "",
                cacheTime
            )
            updateUser(updatedUser)
            updatedUser
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
        val user = prefUser.first()
        if (user.username.isEmpty()) return null
        return UserEndpoint.getUser(user.username)
    }

    suspend fun updateUser(partialUser: PartialUser) {
        context.localUser.edit {
            it[usernameKey] = partialUser.username
            it[pfpKey] = partialUser.imageUrl
            it[expiresKey] = partialUser.expiresIn
        }
    }

    private val usernameKey = stringPreferencesKey("username")
    private val pfpKey = stringPreferencesKey("profilePictureUrl")
    private val expiresKey = longPreferencesKey("expires_in")
    private val cacheTime = Date(Date().time + (1000 * 60 * 60 * 48)).time
}