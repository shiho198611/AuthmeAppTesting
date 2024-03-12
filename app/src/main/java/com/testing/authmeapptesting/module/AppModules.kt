package com.testing.authmeapptesting.module

import com.testing.authmeapptesting.viewmodel.GithubUserProfileViewModel
import com.testing.authmeapptesting.viewmodel.ListGithubUsersViewModel
import com.testing.authmeapptesting.viewmodel.SearchGithubUserViewModel
import com.testing.githubusersdk.data.repository.GithubUsersRepository
import com.testing.githubusersdk.data.repository.SearchUsersRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel{ListGithubUsersViewModel()}
    viewModel{GithubUserProfileViewModel()}
    viewModel{SearchGithubUserViewModel()}
}

val repositoryModule = module {
    factory {
        GithubUsersRepository()
    }
    factory {
        SearchUsersRepository()
    }
}