package io.musicorum.mobile.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import io.musicorum.mobile.components.CenteredLoadingSpinner
import io.musicorum.mobile.viewmodels.AccountVm
import io.musicorum.mobile.views.individual.User

@Composable
fun Account(model: AccountVm = viewModel()) {

    val user = model.user.observeAsState(null).value

    if (user == null) {
        CenteredLoadingSpinner()
    } else {
        User(username = user.username)
    }
}