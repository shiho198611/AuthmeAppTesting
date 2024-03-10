package com.testing.githubusersdk.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.testing.githubusersdk.data.locale.GitHubUserEntity
import com.testing.githubusersdk.data.locale.GitHubUserProfileEntity
import com.testing.githubusersdk.data.locale.GithubUserPlanEntity

@Dao
interface GithubUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGithubUsers(data: List<GitHubUserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGithubUserProfiles(data: List<GitHubUserProfileEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGithubUserPlan(data: List<GithubUserPlanEntity>)

    @Query("SELECT * FROM github_user")
    suspend fun getSimplyGithubUsers(): List<GitHubUserEntity>

    @Query("select * from github_user where github_user.node_id = :nodeId")
    suspend fun getSimplyGithubUser(nodeId: String): GitHubUserEntity

    @Query("select * from github_user join github_user_profile on github_user.node_id = github_user_profile.node_id join github_user_plan on github_user.node_id = github_user_plan.node_id")
    suspend fun getGithubUsersCompleteData(): List<Map<GitHubUserEntity, GitHubUserProfileEntity>>

    @Query("select * from github_user join github_user_profile on github_user.node_id = github_user_profile.node_id join github_user_plan on github_user.node_id = github_user_plan.node_id where github_user.node_id = :nodeId")
    suspend fun getSpecificGithubUserCompleteData(nodeId: String): Map<GitHubUserEntity, GitHubUserProfileEntity>

    @Query("select * from github_user_profile where github_user_profile.node_id = :nodeId")
    suspend fun getSpecificGithubUserProfile(nodeId: String): GitHubUserProfileEntity

    @Query("select * from github_user_plan where github_user_plan.node_id = :nodeId")
    suspend fun getSpecificGithubUserPlan(nodeId: String): GithubUserPlanEntity

}