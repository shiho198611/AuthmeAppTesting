package com.testing.authmeapptesting

import android.content.Context
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.navigation.NavHostController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.testing.authmeapptesting.constants.CommonTestTag.GITHUB_USER_STAFF_BACKGROUND
import com.testing.authmeapptesting.constants.ListGithubUsersScreenTestTag.LIST_GITHUB_USERS
import com.testing.authmeapptesting.constants.ListGithubUsersScreenTestTag.SEARCH_BUTTON
import com.testing.authmeapptesting.constants.NavigationConstants.SCREEN_SEARCH_USERS
import com.testing.authmeapptesting.constants.NavigationConstants.SCREEN_USER_PROFILE
import com.testing.authmeapptesting.ui.screen.ListGithubUsersScreen
import com.testing.authmeapptesting.viewmodel.ListGithubUsersViewModel
import com.testing.githubusersdk.data.locale.GitHubUserEntity
import com.testing.githubusersdk.data.repository.GithubUsersRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class ListGithubUsersViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var mockGithubUsersRepository: GithubUsersRepository

    @RelaxedMockK
    private lateinit var mockNavHostController: NavHostController

    private lateinit var listGithubUsersViewModel: ListGithubUsersViewModel

    private val loginPattern = "dummy_login_"

    @Before
    fun initTest() {

        listGithubUsersViewModel = ListGithubUsersViewModel()

        startKoin {
            allowOverride(true)
            modules(
                module {
                    factory {
                        mockGithubUsersRepository
                    }
                }
            )
        }
    }

    @Test
    fun listGithubUsersHasLoginTextTest() {
        val itemCnt = 30
        val dummyPaging = PagingData.from(createDummyGithubUsersPagingData(itemCnt))

        every { mockGithubUsersRepository.getGithubUsers() } returns flowOf(dummyPaging)

        composeTestRule.setContent {
            ListGithubUsersScreen(
                navController = mockNavHostController,
                viewModel = listGithubUsersViewModel
            )
        }

        composeTestRule.onNodeWithTag(LIST_GITHUB_USERS)
            .onChildren()
            .onFirst()
            .assert(hasText("${loginPattern}1"))
    }

    @Test
    fun listGithubUsersCountTest() {
        val itemCnt = 30
        val dummyPaging = PagingData.from(createDummyGithubUsersPagingData(itemCnt))

        every { mockGithubUsersRepository.getGithubUsers() } returns flowOf(dummyPaging)

        composeTestRule.setContent {
            ListGithubUsersScreen(
                navController = mockNavHostController,
                viewModel = listGithubUsersViewModel
            )
        }

        composeTestRule.onNodeWithTag(LIST_GITHUB_USERS)
            .performScrollToNode(hasText("$loginPattern$itemCnt"))
        composeTestRule.onNodeWithTag("$loginPattern$itemCnt").assertExists().assertIsDisplayed()
    }

    @Test
    fun listGithubUsersHasShowStaffTag() {
        val itemCnt = 30
        val dummyPaging = PagingData.from(createDummyGithubUsersPagingData(itemCnt))

        every { mockGithubUsersRepository.getGithubUsers() } returns flowOf(dummyPaging)

        composeTestRule.setContent {
            ListGithubUsersScreen(
                navController = mockNavHostController,
                viewModel = listGithubUsersViewModel
            )
        }

        composeTestRule.onNodeWithTag(LIST_GITHUB_USERS)
            .performScrollToNode(hasText("$loginPattern$itemCnt"))
        composeTestRule.onNodeWithTag("$loginPattern$itemCnt").assertExists()

        val context: Context = ApplicationProvider.getApplicationContext()
        composeTestRule.onNodeWithTag("$loginPattern$itemCnt")
            .assert(hasText(context.getString(R.string.text_staff)))

        composeTestRule.onNodeWithTag(GITHUB_USER_STAFF_BACKGROUND, useUnmergedTree = true)
            .assertExists()
            .assertIsDisplayed()

    }

    @Test
    fun listGithubUsersIsSearchButtonShowTest() {
        val itemCnt = 30
        val dummyPaging = PagingData.from(createDummyGithubUsersPagingData(itemCnt))

        every { mockGithubUsersRepository.getGithubUsers() } returns flowOf(dummyPaging)

        composeTestRule.setContent {
            ListGithubUsersScreen(
                navController = mockNavHostController,
                viewModel = listGithubUsersViewModel
            )
        }

        composeTestRule.onRoot()
            .onChildren()
            .onFirst()
            .assertIsDisplayed()
            .assert(hasClickAction())
    }

    @Test
    fun listGithubUsersSearchButtonClickTest() {
        val itemCnt = 30
        val dummyPaging = PagingData.from(createDummyGithubUsersPagingData(itemCnt))

        every { mockGithubUsersRepository.getGithubUsers() } returns flowOf(dummyPaging)

        composeTestRule.setContent {
            ListGithubUsersScreen(
                navController = mockNavHostController,
                viewModel = listGithubUsersViewModel
            )
        }

        composeTestRule.onNodeWithTag(SEARCH_BUTTON)
            .assertHasClickAction()
            .performClick()

        verify { mockNavHostController.navigate(SCREEN_SEARCH_USERS) }
    }

    @Test
    fun gotoUserProfileScreenTest() {
        val itemCnt = 30
        val dummyPaging = PagingData.from(createDummyGithubUsersPagingData(itemCnt))

        every { mockGithubUsersRepository.getGithubUsers() } returns flowOf(dummyPaging)

        composeTestRule.setContent {
            ListGithubUsersScreen(
                navController = mockNavHostController,
                viewModel = listGithubUsersViewModel
            )
        }

        composeTestRule.onNodeWithTag(LIST_GITHUB_USERS)
            .onChildren()
            .onFirst()
            .performClick()

        verify { mockNavHostController.navigate("${SCREEN_USER_PROFILE}/${loginPattern}1") }
    }

    @After
    fun endTest() {
        stopKoin()
    }

    fun createDummyGithubUsersPagingData(size: Int): MutableList<GitHubUserEntity> {
        val dummyList = mutableListOf<GitHubUserEntity>()

        for (i in 1..size) {

            val dummyUser = if (i == 30) {
                GitHubUserEntity(
                    "${loginPattern}$i", "http://fakeavatarurl.com", true, i
                )
            } else {
                GitHubUserEntity(
                    "${loginPattern}$i", "http://fakeavatarurl.com", null, i
                )
            }

            dummyList.add(dummyUser)
        }

        return dummyList
    }


}