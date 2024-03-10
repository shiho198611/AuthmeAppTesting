package com.testing.githubusersdk

import com.testing.githubusersdk.data.remote.GitHubUser
import com.testing.githubusersdk.network.provideApiService
import com.testing.githubusersdk.network.provideRetrofit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@ExperimentalCoroutinesApi
class ExampleUnitTest {

    private val testScope = TestScope()
    private val testDispatcher = StandardTestDispatcher(testScope.testScheduler)

    @Before
    fun initTest() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun endTest() {
        Dispatchers.resetMain()
    }

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)

//        var result: List<GitHubUser> = emptyList()

        testScope.runTest {

            println("gate 1")

            val job = launch {

                println("gate 2")
//                result = plantInfoViewModel.webPageTranslate
//                    .onSubscription {
//                        plantInfoViewModel.gotoWebHelp(verifyUrl)
//                    }.first()
                val httpRes = provideApiService(provideRetrofit()).getGitHubUsers()
                println("status: ${httpRes.code()}")

//                result = ArrayList(provideApiService(provideRetrofit()).getGitHubUsers().body())
            }

            val deferred = async {
                delay(1500)
                job.cancel()
            }
            deferred.await()
        }

//        println("result size: ${result.size}, first: ${result[0].login}")

        Thread.sleep(3000)
    }
}