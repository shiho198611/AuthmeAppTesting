package com.testing.authmeapptesting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.testing.githubusersdk.data.locale.GitHubUserEntity
import com.testing.githubusersdk.data.repository.GithubUsersRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ListGithubUsersViewModel : ViewModel(), KoinComponent {

    private val githubUsersRepository: GithubUsersRepository by inject()

    private var current: Flow<PagingData<GitHubUserEntity>>? = null

    @ExperimentalPagingApi
    fun queryGithubUsers(): Flow<PagingData<GitHubUserEntity>> {
        val lastResult = current
        if (lastResult != null) {
            return lastResult
        }

        val newResult: Flow<PagingData<GitHubUserEntity>> =
            githubUsersRepository.getGithubUsers().cachedIn(viewModelScope)
        current = newResult

        return newResult

    }

}