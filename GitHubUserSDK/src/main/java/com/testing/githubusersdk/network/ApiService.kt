package com.testing.githubusersdk.network

import com.testing.githubusersdk.data.remote.GitHubUser
import com.testing.githubusersdk.data.remote.GithubSearchUsers
import com.testing.githubusersdk.data.remote.GithubUserProfile
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {
    @GET("users")
    suspend fun getGitHubUsers(): Response<List<GitHubUser>>

    @GET
    suspend fun getNextGitHubUsers(@Url next: String): Response<List<GitHubUser>>

    @GET
    suspend fun getNextSearchGitHubUsers(@Url next: String): Response<GithubSearchUsers>

    @GET("search/users")
    suspend fun searchUserByName(@Query("q") searchKey: String): Response<GithubSearchUsers>

    @GET("users/{username}")
    suspend fun getSpecificGitHubUser(@Path("username") login: String): GithubUserProfile?
}