package com.musicorumapp.mobile.states

import androidx.compose.runtime.compositionLocalOf
import com.musicorumapp.mobile.api.models.User

class LocalAuthContent(
    val user: User? = null
)

val LocalAuth = compositionLocalOf {
    LocalAuthContent()
}