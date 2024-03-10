package com.testing.githubusersdk.data.locale

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("github_user_plan")
data class GithubUserPlanEntity (
    @PrimaryKey
    @ColumnInfo(name = "node_id")
    val nodeId: String?,
    @ColumnInfo(name = "collaborators")
    val collaborators: Int?,
    @ColumnInfo(name = "name")
    val name: String?,
    @ColumnInfo(name = "private_repos")
    val privateRepos: Int?,
    @ColumnInfo(name = "space")
    val space: Int?
)