package com.testing.githubusersdk.data.repository

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.testing.githubusersdk.SDKInitialHelper
import com.testing.githubusersdk.TestSDKInitialProxy
import com.testing.githubusersdk.data.db.GithubSDKDatabase
import com.testing.githubusersdk.data.locale.GitHubUserEntity
import com.testing.githubusersdk.data.locale.GitHubUserProfileEntity
import com.testing.githubusersdk.data.remote.GitHubUser
import com.testing.githubusersdk.data.remote.GithubUserProfile
import com.testing.githubusersdk.network.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.Headers
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
class GithubUsersRepositoryTest : KoinTest {

    private val testScope = TestScope()
    private val testDispatcher = StandardTestDispatcher(testScope.testScheduler)
    private lateinit var mockApiService: ApiService
    private lateinit var fakeDatabase: GithubSDKDatabase
    private val githubUsersRepository: GithubUsersRepository by inject()

    private val expectLoginPattern = "dummy_login_"

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
                        GithubUsersRepository()
                    }
                }
            )

        }
    }

    @Test
    fun getGithubUserProfileTest() = runBlocking {

        val dummyProfile = GithubUserProfile(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            expectLoginPattern,
            null,
            null,
            null,
            null,
            null,
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

        Mockito.`when`(mockApiService.getSpecificGitHubUser(Mockito.anyString()))
            .thenReturn(dummyProfile)

        var actual: GitHubUserProfileEntity? = null
        val job: Job?

        job = launch {
            githubUsersRepository.getGithubUserProfile(expectLoginPattern).collect {
                actual = it
            }
        }

        val deferred = async {
            delay(300L)
            job.cancel()
        }
        deferred.await()

        Assert.assertNotNull(actual)
        Assert.assertEquals(expectLoginPattern, actual!!.login)
    }

    @Test
    fun getGithubUserProfileWithNullResponseTest() = runBlocking {
        var actual: GitHubUserProfileEntity? = null
        val job: Job?

        job = launch {
            githubUsersRepository.getGithubUserProfile("").collect {
                actual = it
            }
        }

        val deferred = async {
            delay(300L)
            job.cancel()
        }
        deferred.await()

        Assert.assertNull(actual)
    }

    @Test
    fun getGithubUsersTest() = runTest {

        val dummyUser1 = GitHubUser(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            1,
            "${expectLoginPattern}1",
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
            "${expectLoginPattern}2",
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
        val dummyList = mutableListOf(dummyUser1, dummyUser2)

        val dummyResponse = Response.success(dummyList.toList())

        Mockito.`when`(mockApiService.getGitHubUsers()).thenReturn(dummyResponse)

        var job: Job? = null
        val differProvider = AsyncPagingDifferProvider(diffCallback = object :
            DiffUtil.ItemCallback<GitHubUserEntity>() {
            override fun areItemsTheSame(
                oldItem: GitHubUserEntity,
                newItem: GitHubUserEntity
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: GitHubUserEntity,
                newItem: GitHubUserEntity
            ): Boolean {
                return oldItem == newItem
            }
        })

        job = launch {
            githubUsersRepository.getGithubUsers().collectLatest {
                differProvider.submitData(it, job)
            }
        }

        job.join()

        launch {
            Assert.assertEquals(dummyList.size, differProvider.getActualList().size)

            differProvider.getActualList().forEachIndexed { index, entity ->
                Assert.assertEquals(dummyList[index].login, entity.login)
            }
        }

        advanceUntilIdle()
    }

    @Test
    fun getGithubUsersHttpErrorTest() = runTest {
        val responseBody =
            ResponseBody.create(MediaType.get("application/json"), "{\"message\": \"\"}")
        val dummyResponse = Response.error<List<GitHubUser>>(401, responseBody)

        Mockito.`when`(mockApiService.getGitHubUsers()).thenReturn(dummyResponse)

        var job: Job? = null

        val differProvider = AsyncPagingDifferProvider(diffCallback = object :
            DiffUtil.ItemCallback<GitHubUserEntity>() {
            override fun areItemsTheSame(
                oldItem: GitHubUserEntity,
                newItem: GitHubUserEntity
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: GitHubUserEntity,
                newItem: GitHubUserEntity
            ): Boolean {
                return oldItem == newItem
            }
        })


        job = launch {
            githubUsersRepository.getGithubUsers().collectLatest {
                differProvider.submitData(it, job!!)
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
    fun getGithubUsersWithPaging() = runTest {
        val dummyNextHeaderUrl = "https://dummynext.com"

        val dummyUsersPage1 = createDummyGithubUsers(1, 40)
        val dummyUsersPage2 = createDummyGithubUsers(41, 60)

        val header = Headers.of("Link", "<https://dummynext.com>; rel=\"next\"")
        val dummyPage1Response = Response.success(dummyUsersPage1.toList(), header)
        val dummyPage2Response = Response.success(dummyUsersPage2.toList())

        Mockito.`when`(mockApiService.getGitHubUsers()).thenReturn(dummyPage1Response)
        Mockito.`when`(mockApiService.getNextGitHubUsers(dummyNextHeaderUrl))
            .thenReturn(dummyPage2Response)

        var job: Job? = null

        val differProvider = AsyncPagingDifferProvider(diffCallback = object :
            DiffUtil.ItemCallback<GitHubUserEntity>() {
            override fun areItemsTheSame(
                oldItem: GitHubUserEntity,
                newItem: GitHubUserEntity
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: GitHubUserEntity,
                newItem: GitHubUserEntity
            ): Boolean {
                return oldItem == newItem
            }
        })

        job = launch {
            githubUsersRepository.getGithubUsers().collectLatest {
                differProvider.submitData(it, job!!)
            }
        }

        job.join()

        launch {
            Assert.assertEquals(60, differProvider.getActualList().size)

            differProvider.getActualList().forEachIndexed { index, entity ->
                Assert.assertEquals("${expectLoginPattern}${index + 1}", entity.login)
            }
        }

        advanceUntilIdle()
    }

    @After
    fun closeDb() {
        Dispatchers.resetMain()
        fakeDatabase.clearAllTables()
        fakeDatabase.close()
        stopKoin()
    }

    private fun createDummyGithubUsers(startIndex: Int, endIndex: Int): ArrayList<GitHubUser> {
        val dummyUsers = ArrayList<GitHubUser>()
        for (i in startIndex..endIndex) {
            val dummyUser = GitHubUser(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                i,
                "${expectLoginPattern}${i}",
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
            dummyUsers.add(dummyUser)
        }
        return dummyUsers
    }
}