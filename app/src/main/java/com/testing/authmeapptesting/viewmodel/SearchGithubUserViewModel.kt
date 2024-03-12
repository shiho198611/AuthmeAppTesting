package com.testing.authmeapptesting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.testing.githubusersdk.data.locale.GithubUserCompleteEntity
import com.testing.githubusersdk.data.repository.SearchUsersRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SearchGithubUserViewModel : ViewModel(), KoinComponent {

    private val searchUsersRepository: SearchUsersRepository by inject()

    private var current: Flow<PagingData<GithubUserCompleteEntity>>? = null

    @ExperimentalPagingApi
    fun queryGithubUsers(searchKey: String): Flow<PagingData<GithubUserCompleteEntity>> {

        val newResult = searchUsersRepository.searchGithubUser(searchKey).cachedIn(viewModelScope)

        current = newResult

        return newResult
    }

}