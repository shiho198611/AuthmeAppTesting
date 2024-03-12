package com.testing.githubusersdk.data.locale

import androidx.room.Embedded
import androidx.room.Relation

data class GithubUserCompleteEntity(
    @Embedded val user: GitHubUserEntity,

    @Relation(
        parentColumn = "login",
        entityColumn = "login"
    )
    val profile: GitHubUserProfileEntity
)