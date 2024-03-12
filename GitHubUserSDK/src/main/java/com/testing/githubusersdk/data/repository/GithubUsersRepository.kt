package com.testing.githubusersdk.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.testing.githubusersdk.SDKInitialHelper
import com.testing.githubusersdk.data.db.GithubSDKDatabase
import com.testing.githubusersdk.data.locale.GitHubUserEntity
import com.testing.githubusersdk.data.locale.GitHubUserProfileEntity
import com.testing.githubusersdk.data.remotemediator.GetGithubUserMediator
import com.testing.githubusersdk.extension.transferToDBEntity
import com.testing.githubusersdk.network.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class GithubUsersRepository : KoinComponent {

    private val apiService: ApiService by SDKInitialHelper.getKoinContext().koin.inject()
    private val db: GithubSDKDatabase by SDKInitialHelper.getKoinContext().koin.inject()

    @ExperimentalPagingApi
    fun getGithubUsers(): Flow<PagingData<GitHubUserEntity>> {

        val githubUserMediator = GetGithubUserMediator(apiService, db)

        val userDao = db.githubUserDao()

        val pager = Pager(
            config = PagingConfig(30),
            remoteMediator = githubUserMediator
        ) {
            userDao.getSimplyGithubUsers()
        }

        return pager.flow
    }

    fun getGithubUserProfile(login: String): Flow<GitHubUserProfileEntity> {

        CoroutineScope(Dispatchers.IO).launch {
            val profileEntity = db.githubUserDao().getSpecificGithubUserProfile(login)
            if (profileEntity == null) {
                val profile = apiService.getSpecificGitHubUser(login)
                val entity = profile?.transferToDBEntity()
                if (entity != null) {
                    db.githubUserDao().insertGithubUserProfiles(entity)
                }
            }
        }

        return db.githubUserDao().getSpecificGithubUserProfileFlow(login)
    }

}