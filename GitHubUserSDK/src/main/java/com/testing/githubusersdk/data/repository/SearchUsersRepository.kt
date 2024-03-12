package com.testing.githubusersdk.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.testing.githubusersdk.SDKInitialHelper
import com.testing.githubusersdk.data.db.GithubSDKDatabase
import com.testing.githubusersdk.data.locale.GithubUserCompleteEntity
import com.testing.githubusersdk.data.remotemediator.SearchGithubUserMediator
import com.testing.githubusersdk.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.koin.core.component.KoinComponent

class SearchUsersRepository : KoinComponent {

    private val apiService: ApiService by SDKInitialHelper.getKoinContext().koin.inject()
    private val db: GithubSDKDatabase by SDKInitialHelper.getKoinContext().koin.inject()

    @ExperimentalPagingApi
    fun searchGithubUser(searchKey: String): Flow<PagingData<GithubUserCompleteEntity>> {

        if (searchKey.isEmpty()) return flowOf(PagingData.empty())

        val searchGithubUserMediator = SearchGithubUserMediator(apiService, db, searchKey)

        val userDao = db.githubUserDao()

        val pager = Pager(
            config = PagingConfig(30),
            remoteMediator = searchGithubUserMediator
        ) {
            userDao.searchGithubUsersByName(searchKey)
        }

        return pager.flow

    }
}