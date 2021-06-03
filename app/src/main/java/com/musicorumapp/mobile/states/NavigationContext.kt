package com.musicorumapp.mobile.states

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController

class LocalNavigationContextContent(
    val navigationController: NavHostController? = null
)

val LocalNavigationContext = compositionLocalOf {
    LocalNavigationContextContent()
}