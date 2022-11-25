package io.musicorum.mobile.screens

import androidx.compose.runtime.Composable
import io.musicorum.mobile.LocalUser
import io.musicorum.mobile.screens.individual.User

@Composable
fun Account() {
    val user = LocalUser.current
    User(username = user!!.user.name)
}