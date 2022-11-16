package io.musicorum.mobile.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import io.musicorum.mobile.LocalUser
import io.musicorum.mobile.screens.individual.User

@Composable
fun Account(nav: NavHostController) {
    val user = LocalUser.current
    User(username = user!!.user.name, nav = nav)
}