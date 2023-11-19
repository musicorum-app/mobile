package io.musicorum.mobile.views.login

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import io.musicorum.mobile.BuildConfig
import io.musicorum.mobile.datastore.AnalyticsConsent
import io.musicorum.mobile.datastore.UserData
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.serialization.User
import io.musicorum.mobile.userData
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserConfirmationViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    private val Context.analyticsConsent: DataStore<Preferences> by preferencesDataStore(name = AnalyticsConsent.DataStoreName)
    val ctx = application
    val user = MutableLiveData<User?>(null)

    fun setAnalyticsPreferences(consent: Boolean) {
        if (BuildConfig.DEBUG) return
        Firebase.analytics.setAnalyticsCollectionEnabled(consent)
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(consent)
        viewModelScope.launch {
            ctx.analyticsConsent.edit {
                it[AnalyticsConsent.CONSENT_KEY] = consent
            }
        }
    }

    fun saveSession(token: String) {
        viewModelScope.launch {
            ctx.userData.edit {
                it[UserData.SESSION_KEY] = token
            }
        }
    }

    init {
        val sessionKey = savedStateHandle.get<String>("session_key")!!
        viewModelScope.launch {
            val sessionUser = UserEndpoint.getSessionUser(sessionKey)
            user.value = sessionUser
        }
    }
}