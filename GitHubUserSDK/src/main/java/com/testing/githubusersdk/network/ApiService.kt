package com.testing.githubusersdk.network

import com.testing.githubusersdk.data.remote.GitHubUser
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.http.QueryName

interface ApiService {
    @Headers(value = [
        "Accept: application/vnd.github+json",
        "Authorization: Bearer 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",
        "X-GitHub-Api-Version: 2022-11-28"
    ])
    @GET("users")
    suspend fun getGitHubUsers(): Response<List<GitHubUser>>

    @Headers(value = [
        "Accept: application/vnd.github+json",
        "Authorization: Bearer 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",
        "X-GitHub-Api-Version: 2022-11-28"
    ])
    @GET("search/users")
    suspend fun searchUserByName(@Query("q") queryName: String): Response<List<GitHubUser>>


}