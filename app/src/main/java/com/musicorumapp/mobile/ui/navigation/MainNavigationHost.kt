package com.musicorumapp.mobile.ui.navigation

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.musicorumapp.mobile.states.LocalNavigationContext
import com.musicorumapp.mobile.states.LocalNavigationContextContent
import com.musicorumapp.mobile.states.models.AuthenticationViewModel
import com.musicorumapp.mobile.states.models.DiscoverPageViewModel
import com.musicorumapp.mobile.states.models.HomePageViewModel
import com.musicorumapp.mobile.ui.components.PageAnimation
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

    CompositionLocalProvider(
        LocalNavigationContext provides LocalNavigationContextContent(
            navController
        )
    ) {
        NavHost(navController = navController, startDestination = Page.Home.name) {
            composable(Page.Home.name) {
                PageAnimation {
                    HomePage(
                    authenticationViewModel = authenticationViewModel,
                    homePageViewModel = homePageViewModel
                )
                }
            }
            composable(Page.Scrobbling.name) {
                PageAnimation {
                    Text("Scrobble")
                }
            }
            composable(Page.Charts.name) {
                PageAnimation {
                    Text("Charts")
                }
            }
            composable(Page.Profile.name) {
                PageAnimation {
                    Text("Profile")
                }
            }



            composable(ComposableRoutes.Search) {
                PageAnimation {
                    DiscoverPage(
                    authenticationViewModel = authenticationViewModel,
                    discoverPageViewModel = discoverPageViewModel
                )
                }
            }
        }
    }
}

object ComposableRoutes {
    const val Search = "search"
}