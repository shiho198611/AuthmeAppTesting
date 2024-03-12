package com.testing.authmeapptesting.viewmodel

import androidx.lifecycle.ViewModel
import com.testing.githubusersdk.data.locale.GitHubUserProfileEntity
import com.testing.githubusersdk.data.repository.GithubUsersRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GithubUserProfileViewModel : ViewModel(), KoinComponent {

    private val usersRepository: GithubUsersRepository by inject()

    fun getGithubUserProfileDataFlow(login: String): Flow<GitHubUserProfileEntity> {
        return usersRepository.getGithubUserProfile(login)
    }
}