package com.testing.githubusersdk.network

import okhttp3.Interceptor
import okhttp3.Response

class SDKAuthInterceptor(private val authorization: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest = chain.request()

        val newRequest = originRequest.newBuilder()
            .addHeader("Accept", "application/vnd.github+json")
            .addHeader("Authorization", "Bearer $authorization")
            .addHeader("X-GitHub-Api-Version", "2022-11-28")
            .build()
        return chain.proceed(newRequest)
    }
}