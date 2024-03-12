package com.testing.githubusersdk.data.locale

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("github_user_profile")
data class GitHubUserProfileEntity(
    @PrimaryKey
    @ColumnInfo(name = "login")
    val login: String,
    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String?,
    @ColumnInfo(name = "bio")
    val bio: String?,
    @ColumnInfo(name = "blog")
    val blog: String?,
    @ColumnInfo(name = "location")
    val location: String?,
    @ColumnInfo(name = "name")
    val name: String?,
    @ColumnInfo(name = "site_admin")
    val siteAdmin: Boolean?
)