package com.testing.githubusersdk.data.remotemediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.testing.githubusersdk.data.db.GithubSDKDatabase
import com.testing.githubusersdk.data.locale.FUNCTION_SEARCH
import com.testing.githubusersdk.data.locale.GitHubUserProfileEntity
import com.testing.githubusersdk.data.locale.GithubUserCompleteEntity
import com.testing.githubusersdk.data.locale.RemoteKey
import com.testing.githubusersdk.extension.parseNextUrl
import com.testing.githubusersdk.extension.transferToDBEntity
import com.testing.githubusersdk.network.ApiService

@ExperimentalPagingApi
class SearchGithubUserMediator(
    private val apiService: ApiService,
    private val db: GithubSDKDatabase,
    private val searchKey: String
) : RemoteMediator<Int, GithubUserCompleteEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, GithubUserCompleteEntity>
    ): MediatorResult {

        if (searchKey.isEmpty()) return MediatorResult.Success(endOfPaginationReached = true)

        try {

            val remoteKeyDao = db.remoteKeyDao()

            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = db.withTransaction {
                        remoteKeyDao.remoteKeyByQuery(FUNCTION_SEARCH)
                    }

                    if (remoteKey.nextKey == null && state.lastItemOrNull() != null) {
                        return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    }

                    remoteKey.nextKey
                }
            }

            val response = if (loadKey == null) {
                apiService.searchUserByName(searchKey)
            } else {
                apiService.getNextSearchGitHubUsers(loadKey)
            }

            if (response.isSuccessful && response.body() != null) {

                db.withTransaction {
                    val entities = response.body()!!.items.mapNotNull {
                        it.transferToDBEntity()
                    }

                    for (entity in entities) {
                        if (!entity.login.contains(searchKey)) {
                            var profileEntity =
                                db.githubUserDao().getSpecificGithubUserProfile(entity.login)
                            if (profileEntity == null) {
                                profileEntity = GitHubUserProfileEntity(
                                    entity.login,
                                    null,
                                    null,
                                    null,
                                    null,
                                    searchKey,
                                    null
                                )
                            }

                            db.githubUserDao().insertGithubUserProfiles(profileEntity)
                        }
                    }

                    db.githubUserDao().insertGithubUsers(entities)
                }

                val link = response.headers().get("Link")
                val nextUrl = link.parseNextUrl()

                if (loadType == LoadType.REFRESH) {
                    remoteKeyDao.deleteByQuery(FUNCTION_SEARCH)
                }

                remoteKeyDao.insertOrReplace(
                    RemoteKey(FUNCTION_SEARCH, nextUrl)
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