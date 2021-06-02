package com.musicorumapp.mobile.ui.navigation

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.musicorumapp.mobile.states.models.AuthenticationViewModel
import com.musicorumapp.mobile.states.models.DiscoverPageViewModel
import com.musicorumapp.mobile.states.models.HomePageViewModel
import com.musicorumapp.mobile.ui.components.Title
import com.musicorumapp.mobile.ui.pages.DiscoverPage
import com.musicorumapp.mobile.ui.pages.HomePage

@Composable
fun MainNavigationHost(
    navController: NavHostController,
    authenticationViewModel: AuthenticationViewModel?
) {

    val discoverPageViewModel: DiscoverPageViewModel =
        viewModel(factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return DiscoverPageViewModel(authenticationViewModel!!) as T
            }
        })

    val homePageViewModel: HomePageViewModel =
        viewModel(factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return HomePageViewModel(authenticationViewModel!!) as T
            }
        })

    NavHost(navController = navController, startDestination = Page.Home.name) {
        composable(Page.Home.name) {
             HomePage(authenticationViewModel = authenticationViewModel, homePageViewModel = homePageViewModel)
        }
        composable(Page.Discover.name) {
            DiscoverPage(authenticationViewModel = authenticationViewModel, discoverPageViewModel = discoverPageViewModel)
        }
        composable(Page.Scrobbling.name) {
            Text("Scrobble")
        }
        composable(Page.Charts.name) {
            Text("Charts")
        }
        composable(Page.Profile.name) {
            Text("Profile")
        }
    }
}