package io.musicorum.mobile.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.models.PartialUser
import io.musicorum.mobile.serialization.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.localUser: DataStore<Preferences> by preferencesDataStore("partialUser")

class LocalUserRepository(val context: Context) {
    val partialUser: Flow<PartialUser> =
        context.localUser.data.map {
            PartialUser(
                it[usernameKey] ?: "",
                it[pfpKey] ?: ""
            )
        }

    suspend fun fetch(): User? {
        val user = partialUser.first()
        if (user.username.isEmpty()) return null
        return UserEndpoint.getUser(user.username)
    }

    suspend fun updateUser(partialUser: PartialUser) {
        context.localUser.edit {
            it[usernameKey] = partialUser.username
            it[pfpKey] = partialUser.imageUrl
        }
    }

    private val usernameKey = stringPreferencesKey("username")
    private val pfpKey = stringPreferencesKey("profilePictureUrl")
}