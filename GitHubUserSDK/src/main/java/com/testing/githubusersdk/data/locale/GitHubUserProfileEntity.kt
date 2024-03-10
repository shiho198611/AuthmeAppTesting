package com.testing.githubusersdk.data.locale

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("github_user_profile")
data class GitHubUserProfileEntity (
    @PrimaryKey
    @ColumnInfo(name = "node_id")
    val nodeId: String?,
    @ColumnInfo(name = "bio")
    val bio: Any?,
    @ColumnInfo(name = "blog")
    val blog: String?,
    @ColumnInfo(name = "collaborators")
    val collaborators: Int?,
    @ColumnInfo(name = "company")
    val company: Any?,
    @ColumnInfo(name = "created_at")
    val createdAt: String?,
    @ColumnInfo(name = "disk_usage")
    val diskUsage: Int?,
    @ColumnInfo(name = "email")
    val email: Any?,
    @ColumnInfo(name = "followers")
    val followers: Int?,
    @ColumnInfo(name = "following")
    val following: Int?,
    @ColumnInfo(name = "hireable")
    val hireable: Any?,
    @ColumnInfo(name = "location")
    val location: Any?,
    @ColumnInfo(name = "name")
    val name: Any?,
    @ColumnInfo(name = "owned_private_repos")
    val ownedPrivateRepos: Int?,
    @ColumnInfo(name = "private_gists")
    val privateGists: Int?,
    @ColumnInfo(name = "public_gists")
    val publicGists: Int?,
    @ColumnInfo(name = "public_repos")
    val publicRepos: Int?,
    @ColumnInfo(name = "total_private_repos")
    val totalPrivateRepos: Int?,
    @ColumnInfo(name = "twitter_username")
    val twitterUsername: Any?,
    @ColumnInfo(name = "two_factor_authentication")
    val twoFactorAuthentication: Boolean?,
    @ColumnInfo(name = "updated_at")
    val updatedAt: String?
)