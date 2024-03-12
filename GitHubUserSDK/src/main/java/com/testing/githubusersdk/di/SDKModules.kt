package com.testing.githubusersdk.di

import androidx.room.Room
import com.testing.githubusersdk.BuildConfig
import com.testing.githubusersdk.data.db.DATABASE_NAME
import com.testing.githubusersdk.data.db.GithubSDKDatabase
import com.testing.githubusersdk.network.ApiService
import com.testing.githubusersdk.network.SDKAuthInterceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

fun networkModule(authToken: String) = module {
    factory {
        SDKAuthInterceptor(authToken)
    }

    factory {
        OkHttpClient.Builder().addInterceptor(get<SDKAuthInterceptor>()).build()
    }

    single {
        Retrofit.Builder().client(get<OkHttpClient>())
            .addConverterFactory(MoshiConverterFactory.create()).baseUrl(BuildConfig.API_ROOT)
            .build()
    }

    factory { get<Retrofit>().create(ApiService::class.java) }

}

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(), GithubSDKDatabase::class.java, DATABASE_NAME
        ).build()
    }

    single {
        get<GithubSDKDatabase>().githubUserDao()
    }
}