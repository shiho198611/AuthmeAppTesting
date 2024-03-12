package com.testing.githubusersdk.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GithubSearchUsers(

    @Json(name = "total_count")
    val totalCount: Int?,

    @Json(name = "incomplete_results")
    val incompleteResults: Boolean?,

    @Json(name = "items")
    val items: List<GitHubUser>

)