package com.testing.githubusersdk

import android.content.Context
import com.testing.githubusersdk.di.databaseModule
import com.testing.githubusersdk.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication

class SDKInitialFactoryImpl(private val context: Context, private val authToken: String) :
    SDKInitialFactory {

    override fun defineKoinInject(): KoinApplication {
        return koinApplication {
            androidContext(context)
            modules(networkModule(authToken))
            modules(databaseModule)
        }
    }
}