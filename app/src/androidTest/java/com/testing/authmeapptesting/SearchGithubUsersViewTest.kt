package com.testing.authmeapptesting

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.testing.authmeapptesting.constants.CommonTestTag.CLOSE_SCREEN
import com.testing.authmeapptesting.constants.NavigationConstants.SCREEN_USER_PROFILE
import com.testing.authmeapptesting.constants.SearchGithubUsersScreenTestTag.SEARCH_INPUT
import com.testing.authmeapptesting.constants.SearchGithubUsersScreenTestTag.SEARCH_LIST_USERS
import com.testing.authmeapptesting.ui.screen.SearchGithubUserScreen
import com.testing.authmeapptesting.viewmodel.SearchGithubUserViewModel
import com.testing.githubusersdk.data.locale.GitHubUserEntity
import com.testing.githubusersdk.data.locale.GitHubUserProfileEntity
import com.testing.githubusersdk.data.locale.GithubUserCompleteEntity
import com.testing.githubusersdk.data.repository.SearchUsersRepository
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
class SearchGithubUsersViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var mockSearchUsersRepository: SearchUsersRepository

    @RelaxedMockK
    private lateinit var mockNavHostController: NavHostController

    private lateinit var searchGithubUserViewModel: SearchGithubUserViewModel

    private val loginPattern = "dummy_login_"

    @Before
    fun initTest() {
        searchGithubUserViewModel = SearchGithubUserViewModel()

        startKoin {
            allowOverride(true)
            modules(
                module {
                    factory {
                        mockSearchUsersRepository
                    }
                }
            )
        }
    }

    @Test
    fun searchResultBySearchKeyTest() {
        val itemCnt = 30
        val dummyPaging = PagingData.from(createDummyGithubUsersPagingData(itemCnt))

        every { mockSearchUsersRepository.searchGithubUser("") } returns flowOf()
        every { mockSearchUsersRepository.searchGithubUser("dummy_search_key") } returns flowOf(
            dummyPaging
        )

        composeTestRule.setContent {
            SearchGithubUserScreen(
                navController = mockNavHostController,
                viewModel = searchGithubUserViewModel
            )
        }

        composeTestRule.onNodeWithTag(SEARCH_INPUT)
            .performTextInput("dummy_search_key")

        composeTestRule.onNodeWithTag(SEARCH_LIST_USERS)
            .onChildren()
            .onFirst()
            .assert(hasText("${loginPattern}1"))

        composeTestRule.onNodeWithTag(SEARCH_LIST_USERS)
            .performScrollToNode(hasText("$loginPattern$itemCnt"))

        composeTestRule.waitUntil {
            composeTestRule.onAllNodesWithText("$loginPattern$itemCnt")
                .fetchSemanticsNodes().size == 1
        }

        composeTestRule.onNodeWithTag("$loginPattern$itemCnt").assertExists().assertIsDisplayed()

    }

    @Test
    fun showEmptyListWithEmptySearchKeyTest() {

        every { mockSearchUsersRepository.searchGithubUser("") } returns flowOf()

        composeTestRule.setContent {
            SearchGithubUserScreen(
                navController = mockNavHostController,
                viewModel = searchGithubUserViewModel
            )
        }

        composeTestRule.onNodeWithTag(SEARCH_LIST_USERS)
            .onChildren()
            .onFirst()
            .assertDoesNotExist()
    }

    @Test
    fun exitSearchScreenTest() {
        every { mockSearchUsersRepository.searchGithubUser("") } returns flowOf()

        composeTestRule.setContent {
            SearchGithubUserScreen(
                navController = mockNavHostController,
                viewModel = searchGithubUserViewModel
            )
        }

        composeTestRule.onNodeWithTag(CLOSE_SCREEN)
            .performClick()

        verify { mockNavHostController.popBackStack() }
    }

    @Test
    fun searchGithubUsersGotoProfileTest() {

        val itemCnt = 30
        val dummyPaging = PagingData.from(createDummyGithubUsersPagingData(itemCnt))

        every { mockSearchUsersRepository.searchGithubUser("") } returns flowOf()
        every { mockSearchUsersRepository.searchGithubUser("dummy_search_key") } returns flowOf(
            dummyPaging
        )

        composeTestRule.setContent {
            SearchGithubUserScreen(
                navController = mockNavHostController,
                viewModel = searchGithubUserViewModel
            )
        }

        composeTestRule.onNodeWithTag(SEARCH_INPUT)
            .performTextInput("dummy_search_key")

        composeTestRule.onNodeWithTag(SEARCH_LIST_USERS)
            .onChildren()
            .onFirst()
            .performClick()

        verify { mockNavHostController.navigate("${SCREEN_USER_PROFILE}/${loginPattern}1") }
    }

    @After
    fun endTest() {
        stopKoin()
    }

    fun createDummyGithubUsersPagingData(size: Int): MutableList<GithubUserCompleteEntity> {
        val dummyList = mutableListOf<GithubUserCompleteEntity>()

        for (i in 1..size) {

            val dummyUser = if (i == 30) {
                GitHubUserEntity(
                    "${loginPattern}$i", "http://fakeavatarurl.com", true, null
                )
            } else {
                GitHubUserEntity(
                    "${loginPattern}$i", "http://fakeavatarurl.com", null, null
                )
            }

            val dummyProfile =
                GitHubUserProfileEntity("${loginPattern}$i", null, null, null, null, null, null)

            val completeEntity = GithubUserCompleteEntity(dummyUser, dummyProfile)
            dummyList.add(completeEntity)
        }

        return dummyList
    }

}