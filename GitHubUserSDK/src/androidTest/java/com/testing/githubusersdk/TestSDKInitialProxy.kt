package com.testing.githubusersdk

import com.testing.githubusersdk.data.db.GithubSDKDatabase
import com.testing.githubusersdk.network.ApiService
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class TestSDKInitialProxy(
    private val mockApiService: ApiService,
    private val fakeDatabase: GithubSDKDatabase
) : SDKInitialFactory {

    override fun defineKoinInject(): KoinApplication {
        return koinApplication {
            allowOverride(true)
            modules(
                module {
                    factory<ApiService> {
                        mockApiService
                    }

                    factory<GithubSDKDatabase> {
                        fakeDatabase
                    }
                }
            )
        }
    }

}