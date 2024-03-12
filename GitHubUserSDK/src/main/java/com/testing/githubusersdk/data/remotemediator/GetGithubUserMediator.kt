package com.testing.githubusersdk.data.remotemediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.testing.githubusersdk.data.db.GithubSDKDatabase
import com.testing.githubusersdk.data.locale.FUNCTION_USERS
import com.testing.githubusersdk.data.locale.GitHubUserEntity
import com.testing.githubusersdk.data.locale.RemoteKey
import com.testing.githubusersdk.extension.parseNextUrl
import com.testing.githubusersdk.extension.transferToDBEntity
import com.testing.githubusersdk.network.ApiService

@ExperimentalPagingApi
class GetGithubUserMediator(
    private val apiService: ApiService,
    private val db: GithubSDKDatabase
): RemoteMediator<Int, GitHubUserEntity>() {

    override suspend fun initialize(): InitializeAction {
        return super.initialize()
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, GitHubUserEntity>
    ): MediatorResult {
        try {

            val remoteKeyDao = db.remoteKeyDao()

            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = db.withTransaction {
                        remoteKeyDao.remoteKeyByQuery(FUNCTION_USERS)
                    }

                    if (remoteKey.nextKey == null && state.lastItemOrNull() != null) {
                        return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    }

                    remoteKey.nextKey
                }
            }


            val response = if(loadKey == null) {
                apiService.getGitHubUsers()
            } else {
                apiService.getNextGitHubUsers(loadKey)
            }

            if(response.isSuccessful && response.body() != null) {
                db.withTransaction {
                    val entities = response.body()!!.mapNotNull {
                        it.transferToDBEntity()
                    }
                    db.githubUserDao().insertGithubUsers(entities)
                }

                val link = response.headers().get("Link")
                val nextUrl = link.parseNextUrl()

                if (loadType == LoadType.REFRESH) {
                    remoteKeyDao.deleteByQuery(FUNCTION_USERS)
                }

                remoteKeyDao.insertOrReplace(
                    RemoteKey(FUNCTION_USERS, nextUrl)
                )

                return MediatorResult.Success(
                    endOfPaginationReached = (nextUrl == null)
                )
            } else {
                return MediatorResult.Success(endOfPaginationReached = false)
            }

        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

}
