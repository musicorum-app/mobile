package com.musicorumapp.mobile.states

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf
import com.google.android.material.snackbar.Snackbar

class LocalSnackbarContextContent(
    val snackbarHostState: SnackbarHostState? = null
) {
    suspend fun showSnackBar (message: String) = snackbarHostState?.showSnackbar(message)
}


val LocalSnackbarContext = compositionLocalOf {
    LocalSnackbarContextContent()
}