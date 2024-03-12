package com.testing.githubusersdk.data.repository

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.testing.githubusersdk.SDKInitialHelper
import com.testing.githubusersdk.TestSDKInitialProxy
import com.testing.githubusersdk.data.db.GithubSDKDatabase
import com.testing.githubusersdk.data.locale.GithubUserCompleteEntity
import com.testing.githubusersdk.data.remote.GitHubUser
import com.testing.githubusersdk.data.remote.GithubSearchUsers
import com.testing.githubusersdk.network.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.Mockito
import retrofit2.Response

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
class SearchUserRepositoryTest : KoinTest {

    private val testScope = TestScope()
    private val testDispatcher = StandardTestDispatcher(testScope.testScheduler)
    private lateinit var mockApiService: ApiService
    private lateinit var fakeDatabase: GithubSDKDatabase
    private val searchUsersRepository: SearchUsersRepository by inject()


    @Before
    fun init() {

        Dispatchers.setMain(testDispatcher)

        mockApiService = Mockito.mock(ApiService::class.java)

        val context = ApplicationProvider.getApplicationContext<Context>()
        fakeDatabase = Room.inMemoryDatabaseBuilder(context, GithubSDKDatabase::class.java).build()

        SDKInitialHelper.initSDKInstance(TestSDKInitialProxy(mockApiService, fakeDatabase))

        startKoin {
            allowOverride(true)
            modules(
                module {
                    factory {
                        SearchUsersRepository()
                    }
                }
            )

        }
    }

    @Test
    fun searchGithubUserTest() = runTest {

        val expectPattern = "dummy_login_"
        val dummyUser1 = GitHubUser(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            1,
            "${expectPattern}1",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
        val dummyUser2 = GitHubUser(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            2,
            "${expectPattern}2",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
        val dummyUsers = mutableListOf(dummyUser1, dummyUser2)

        val dummySearchUser = GithubSearchUsers(2, false, dummyUsers.toList())
        val dummyResponse = Response.success(dummySearchUser)

        Mockito.`when`(mockApiService.searchUserByName(Mockito.anyString()))
            .thenReturn(dummyResponse)

        var job: Job? = null
        
        val differProvider = AsyncPagingDifferProvider(
            object : DiffUtil.ItemCallback<GithubUserCompleteEntity>() {
                override fun areItemsTheSame(
                    oldItem: GithubUserCompleteEntity,
                    newItem: GithubUserCompleteEntity
                ): Boolean {
                    return oldItem.user.id == newItem.user.id
                }

                override fun areContentsTheSame(
                    oldItem: GithubUserCompleteEntity,
                    newItem: GithubUserCompleteEntity
                ): Boolean {
                    return oldItem == newItem
                }
            }
        )

        job = launch {
            searchUsersRepository.searchGithubUser("dummy_key").collectLatest {
                differProvider.submitData(it, job)
            }
        }

        job.join()

        launch {
            Assert.assertEquals(dummyUsers.size, differProvider.getActualList().size)

            differProvider.getActualList().forEachIndexed { index, entity ->
                Assert.assertEquals(dummyUsers[index].login, entity.user.login)
            }
        }

        advanceUntilIdle()
    }

    @Test
    fun searchGithubUsersHttpErrorTest() = runTest {
        val responseBody =
            ResponseBody.create(MediaType.get("application/json"), "{\"message\": \"\"}")
        val dummyResponse = Response.error<GithubSearchUsers>(401, responseBody)

        Mockito.`when`(mockApiService.searchUserByName(Mockito.anyString()))
            .thenReturn(dummyResponse)

        var job: Job? = null

        val differProvider = AsyncPagingDifferProvider(
            object : DiffUtil.ItemCallback<GithubUserCompleteEntity>() {
                override fun areItemsTheSame(
                    oldItem: GithubUserCompleteEntity,
                    newItem: GithubUserCompleteEntity
                ): Boolean {
                    return oldItem.user.id == newItem.user.id
                }

                override fun areContentsTheSame(
                    oldItem: GithubUserCompleteEntity,
                    newItem: GithubUserCompleteEntity
                ): Boolean {
                    return oldItem == newItem
                }
            }
        )

        job = launch {
            searchUsersRepository.searchGithubUser("dummy_key").collectLatest {
                differProvider.submitData(it, job)
            }
        }

        val deferred = CoroutineScope(Dispatchers.IO).async {
            delay(3000L)
            job.cancel()
        }
        deferred.await()

        Assert.assertEquals(0, differProvider.getActualList().size)
    }

    @Test
    fun searchGithubUsersEmptyTest() = runTest {
        val dummySearchUser = GithubSearchUsers(2, false, emptyList())
        val dummyResponse = Response.success(dummySearchUser)

        Mockito.`when`(mockApiService.searchUserByName(Mockito.anyString()))
            .thenReturn(dummyResponse)

        var job: Job? = null

        val differProvider = AsyncPagingDifferProvider(
            object : DiffUtil.ItemCallback<GithubUserCompleteEntity>() {
                override fun areItemsTheSame(
                    oldItem: GithubUserCompleteEntity,
                    newItem: GithubUserCompleteEntity
                ): Boolean {
                    return oldItem.user.id == newItem.user.id
                }

                override fun areContentsTheSame(
                    oldItem: GithubUserCompleteEntity,
                    newItem: GithubUserCompleteEntity
                ): Boolean {
                    return oldItem == newItem
                }
            }
        )

        job = launch {
            searchUsersRepository.searchGithubUser("dummy_key").collectLatest {
                differProvider.submitData(it, job)
            }
        }

        val deferred = CoroutineScope(Dispatchers.IO).async {
            delay(3000L)
            job.cancel()
        }
        deferred.await()

        Assert.assertEquals(0, differProvider.getActualList().size)

    }

    @After
    fun closeDb() {
        Dispatchers.resetMain()
        fakeDatabase.clearAllTables()
        fakeDatabase.close()
        stopKoin()
    }
}