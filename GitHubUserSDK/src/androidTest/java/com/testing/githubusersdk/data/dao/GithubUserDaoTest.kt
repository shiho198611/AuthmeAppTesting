package com.testing.githubusersdk.data.dao

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.ExperimentalPagingApi
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.testing.githubusersdk.data.db.GithubSDKDatabase
import com.testing.githubusersdk.data.locale.GitHubUserEntity
import com.testing.githubusersdk.data.locale.GitHubUserProfileEntity
import com.testing.githubusersdk.data.locale.GithubUserCompleteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@ExperimentalPagingApi
class GithubUserDaoTest {

    private lateinit var userDao: GithubUserDao
    private lateinit var db: GithubSDKDatabase

    private val testScope = TestScope()
    private val testDispatcher = StandardTestDispatcher(testScope.testScheduler)

    @get:Rule
    var instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initTest() {
        Dispatchers.setMain(testDispatcher)
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, GithubSDKDatabase::class.java).build()

        userDao = db.githubUserDao()
    }

    @Test
    fun insertAndGetSimplyGithubUserTest() = runTest {
        val expectedIdPattern = "dummy_node_id_"
        val data = GitHubUserEntity("${expectedIdPattern}1", null, null, null)

        val dummyUsers = mutableListOf(data)
        var actual: GitHubUserEntity? = null

        var job: Job? = null
        job = launch {
            userDao.insertGithubUsers(dummyUsers)

            userDao.getSimplyGithubUser("${expectedIdPattern}1").collect {
                actual = it
                job?.cancel()
            }

        }

        advanceUntilIdle()

        Assert.assertNotNull(actual)
        Assert.assertEquals("${expectedIdPattern}1", actual!!.login)
    }

    @Test
    fun insertAndGetGithubUserProfileTest() = runTest {
        val expectedIdPattern = "dummy_node_id_"
        val data = GitHubUserProfileEntity("${expectedIdPattern}1", null, null, null, null, null, null)

        userDao.insertGithubUserProfiles(data)
        var actual: GitHubUserProfileEntity? = null

        var job: Job? = null

        job = launch {
            userDao.getSpecificGithubUserProfileFlow("${expectedIdPattern}1").collect {
                actual = it
                job?.cancel()
            }
        }

        advanceUntilIdle()

        Assert.assertNotNull(actual)
        Assert.assertEquals("${expectedIdPattern}1", actual!!.login)
    }

    @Test
    fun getGithubUserCompleteDataTest() = runTest {
        val expectedIdPattern = "dummy_node_id_"
        val dummyProfileName = "dummy_profile_name"
        val user = GitHubUserEntity("${expectedIdPattern}1", null, null, null)
        val profile = GitHubUserProfileEntity("${expectedIdPattern}1", null, null, null, null, dummyProfileName, null)

        userDao.insertGithubUsers(mutableListOf(user))
        userDao.insertGithubUserProfiles(profile)

        var actual: GithubUserCompleteEntity? = null

        var job: Job? = null
        job = launch {
            userDao.getSpecificGithubUserCompleteData("${expectedIdPattern}1").collect {
                actual = it
                job?.cancel()
            }
        }

        advanceUntilIdle()

        Assert.assertNotNull(actual)
        Assert.assertEquals("${expectedIdPattern}1", actual!!.user.login)
        Assert.assertEquals(dummyProfileName, actual!!.profile.name)
    }

    @After
    fun closeDb() {
        db.clearAllTables()
        db.close()
    }
}