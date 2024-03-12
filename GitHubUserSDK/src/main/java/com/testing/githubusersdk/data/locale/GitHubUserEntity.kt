package com.testing.githubusersdk.data.locale

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "github_user")
data class GitHubUserEntity(
    @PrimaryKey
    @ColumnInfo(name = "login")
    val login: String,
    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String?,
    @ColumnInfo(name = "site_admin")
    val siteAdmin: Boolean?,
    @ColumnInfo(name = "id")
    val id: Int?
)