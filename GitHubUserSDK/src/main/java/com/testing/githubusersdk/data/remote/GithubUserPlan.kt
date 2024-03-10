package com.testing.githubusersdk.data.remote


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GithubUserPlan(
    @Json(name = "collaborators")
    val collaborators: Int?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "private_repos")
    val privateRepos: Int?,
    @Json(name = "space")
    val space: Int?
)