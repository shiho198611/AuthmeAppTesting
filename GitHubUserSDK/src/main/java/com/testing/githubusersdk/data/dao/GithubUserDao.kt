package com.testing.githubusersdk.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.testing.githubusersdk.data.locale.GitHubUserEntity
import com.testing.githubusersdk.data.locale.GitHubUserProfileEntity
import com.testing.githubusersdk.data.locale.GithubUserCompleteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GithubUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGithubUsers(data: List<GitHubUserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGithubUserProfiles(data: GitHubUserProfileEntity)

    @Query("select * from github_user order by github_user.id asc")
    fun getSimplyGithubUsers(): PagingSource<Int, GitHubUserEntity>

    @Query("select * from github_user where github_user.login = :login")
    fun getSimplyGithubUser(login: String): Flow<GitHubUserEntity>

    @Transaction
    @Query("select * from github_user join github_user_profile on github_user.login = github_user_profile.login where github_user.login = :login")
    fun getSpecificGithubUserCompleteData(login: String): Flow<GithubUserCompleteEntity>

    @Query("select * from github_user_profile where github_user_profile.login = :login")
    fun getSpecificGithubUserProfileFlow(login: String): Flow<GitHubUserProfileEntity>

    @Query("select * from github_user_profile where github_user_profile.login = :login")
    suspend fun getSpecificGithubUserProfile(login: String): GitHubUserProfileEntity?

    @Transaction
    @Query("select * from github_user join github_user_profile on github_user.login = github_user_profile.login where github_user.login like '%' || :query || '%' or github_user_profile.name like '%' || :query || '%' order by github_user.id asc")
    fun searchGithubUsersByName(query: String): PagingSource<Int, GithubUserCompleteEntity>

}