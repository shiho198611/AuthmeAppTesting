package com.testing.githubusersdk

import org.koin.core.KoinApplication

object SDKInitialHelper {

    private lateinit var helper: SDKInitialFactory

    fun initSDKInstance(helperInterface: SDKInitialFactory) {
        this.helper = helperInterface
    }

    internal fun getKoinContext(): KoinApplication {
        return helper.defineKoinInject()
    }

}