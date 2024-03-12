package com.testing.authmeapptesting

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.ExperimentalPagingApi
import com.testing.authmeapptesting.constants.NavigationConstants.PARAMETER_LOGIN
import com.testing.authmeapptesting.constants.NavigationConstants.SCREEN_LIST_USERS
import com.testing.authmeapptesting.constants.NavigationConstants.SCREEN_SEARCH_USERS
import com.testing.authmeapptesting.constants.NavigationConstants.SCREEN_USER_PROFILE_PATH
import com.testing.authmeapptesting.module.repositoryModule
import com.testing.authmeapptesting.module.viewModelModule
import com.testing.authmeapptesting.ui.screen.GithubUserProfileScreen
import com.testing.authmeapptesting.ui.screen.ListGithubUsersScreen
import com.testing.authmeapptesting.ui.screen.SearchGithubUserScreen
import com.testing.authmeapptesting.ui.theme.AuthmeAppTestingTheme
import com.testing.authmeapptesting.viewmodel.GithubUserProfileViewModel
import com.testing.authmeapptesting.viewmodel.ListGithubUsersViewModel
import com.testing.authmeapptesting.viewmodel.SearchGithubUserViewModel
import com.testing.githubusersdk.SDKInitialFactoryImpl
import com.testing.githubusersdk.SDKInitialHelper
import org.koin.core.context.startKoin

@ExperimentalPagingApi
class MainActivity : ComponentActivity() {

    private val listGithubUsersViewModel: ListGithubUsersViewModel by viewModels()
    private val githubUserProfileViewModel: GithubUserProfileViewModel by viewModels()
    private val searchGithubUserViewModel: SearchGithubUserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SDKInitialHelper.initSDKInstance(
            SDKInitialFactoryImpl(this, "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")
        )

        startKoin {
            modules(viewModelModule)
            modules(repositoryModule)
        }

        setContent {

            val navController = rememberNavController()

            AuthmeAppTestingTheme {
                Navigation(navController = navController)
            }
        }
    }

    @Composable
    fun Navigation(navController: NavHostController) {
        NavHost(navController, startDestination = SCREEN_LIST_USERS) {
            composable(SCREEN_LIST_USERS) {
                ListGithubUsersScreen(
                    navController,
                    listGithubUsersViewModel
                )
            }
            composable(
                route = SCREEN_USER_PROFILE_PATH,
                arguments = listOf(
                    navArgument(PARAMETER_LOGIN) {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val login = backStackEntry.arguments?.getString(PARAMETER_LOGIN)
                GithubUserProfileScreen(navController, githubUserProfileViewModel, login)
            }
            composable(SCREEN_SEARCH_USERS) {
                SearchGithubUserScreen(
                    navController = navController,
                    viewModel = searchGithubUserViewModel
                )
            }
        }
    }

}

