package io.musicorum.mobile.utils

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf

class LocalSnackbarContext(private val host: SnackbarHostState? = null) {
    suspend fun showSnackbar(message: String, dismissAction: Boolean = false) =
        host?.showSnackbar(message, withDismissAction = dismissAction)
}

val LocalSnackbar = compositionLocalOf { LocalSnackbarContext() }
