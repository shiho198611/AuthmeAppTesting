package com.testing.githubusersdk.extension

import com.testing.githubusersdk.data.locale.GitHubUserEntity
import com.testing.githubusersdk.data.locale.GitHubUserProfileEntity
import com.testing.githubusersdk.data.remote.GitHubUser
import com.testing.githubusersdk.data.remote.GithubUserProfile

fun GitHubUser.transferToDBEntity(): GitHubUserEntity? {
    if (login == null) {
        return null
    }
    return GitHubUserEntity(
        login,
        avatarUrl,
        siteAdmin,
        id
    )
}

fun GithubUserProfile.transferToDBEntity(): GitHubUserProfileEntity? {
    if (login == null) {
        return null
    }
    return GitHubUserProfileEntity(
        login,
        avatarUrl,
        bio,
        blog,
        location,
        name,
        siteAdmin
    )
}