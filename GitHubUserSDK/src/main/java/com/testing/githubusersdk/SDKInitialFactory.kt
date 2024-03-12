package com.testing.githubusersdk

import org.koin.core.KoinApplication

interface SDKInitialFactory {
    fun defineKoinInject(): KoinApplication
}