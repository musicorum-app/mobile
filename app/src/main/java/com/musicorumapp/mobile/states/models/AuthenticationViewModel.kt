package com.musicorumapp.mobile.states.models

import android.util.Log
import androidx.lifecycle.*
import com.musicorumapp.mobile.Constants
import com.musicorumapp.mobile.api.LastfmApi
import com.musicorumapp.mobile.api.models.User
import com.musicorumapp.mobile.authentication.AuthenticationPreferences
import com.musicorumapp.mobile.states.SessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

object AuthenticationValidationState {
    const val NONE = 0
    const val LOGGED_OUT = 1 // LOGIN SCREEN
    const val LOGGED_IN = 2 // MAIN SCREEN LOADED
    const val AUTHENTICATING = 3 // MAIN SCREEN LOGGING IN
}
@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val authPrefs: AuthenticationPreferences,
    private val sessionState: SessionState
): ViewModel() {
    private val _user = MutableLiveData<User?>(null)
    private val _authenticationValidationState = MutableLiveData(AuthenticationValidationState.AUTHENTICATING)

    val user: LiveData<User?> = _user
    val authenticationValidationState: LiveData<Int> = _authenticationValidationState

    fun setAuthenticationValidationState (state: Int) {
        _authenticationValidationState.value = state
    }

    fun authenticateFromToken (token: String, showSnackBar: (String) -> Unit) {
        viewModelScope.launch {
            val res = LastfmApi.getAuthEndpoint().getSession(token)

            authPrefs.setLastfmSessionToken(res.key)
            fetchUser(showSnackBar)
        }
    }

    fun fetchUser (
        showSnackBar: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val usr = LastfmApi.getUserEndpoint().getUserInfoFromToken(authPrefs.getLastfmSessionToken().orEmpty()).toUser()
                _user.value = usr
                sessionState.currentUser = usr
                _authenticationValidationState.value = AuthenticationValidationState.LOGGED_IN
            } catch (e: Exception) {
                Log.e(Constants.LOG_TAG, e.toString())
                showSnackBar("Could not fetch user data!")
            }
        }
    }

    fun setUser(usr: User) {
        _user.value = usr
    }
}