package com.testing.authmeapptesting

import android.content.Context
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.testing.authmeapptesting.constants.CommonTestTag.CLOSE_SCREEN
import com.testing.authmeapptesting.constants.CommonTestTag.GITHUB_USER_STAFF
import com.testing.authmeapptesting.constants.CommonTestTag.GITHUB_USER_STAFF_BACKGROUND
import com.testing.authmeapptesting.constants.GithubUserProfileScreenTestTag.PROFILE_BIO
import com.testing.authmeapptesting.constants.GithubUserProfileScreenTestTag.PROFILE_BLOG
import com.testing.authmeapptesting.constants.GithubUserProfileScreenTestTag.PROFILE_LOCATION
import com.testing.authmeapptesting.constants.GithubUserProfileScreenTestTag.PROFILE_LOGIN
import com.testing.authmeapptesting.constants.GithubUserProfileScreenTestTag.PROFILE_NAME
import com.testing.authmeapptesting.constants.GithubUserProfileScreenTestTag.PROFILE_NO_BLOG
import com.testing.authmeapptesting.constants.GithubUserProfileScreenTestTag.PROFILE_NO_CONTENT
import com.testing.authmeapptesting.ui.screen.GithubUserProfileScreen
import com.testing.authmeapptesting.viewmodel.GithubUserProfileViewModel
import com.testing.githubusersdk.data.locale.GitHubUserProfileEntity
import com.testing.githubusersdk.data.repository.GithubUsersRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

@RunWith(AndroidJUnit4::class)
class GithubUserProfileViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var mockGithubUsersRepository: GithubUsersRepository

    @RelaxedMockK
    private lateinit var mockNavHostController: NavHostController

    private lateinit var githubUserProfileViewModel: GithubUserProfileViewModel

    private val dummyLogin = "dummy_login"
    private val dummyAvatarUrl = "http://dummyavatarurl.com"
    private val dummyBio = "dummy_bio"
    private val dummyLocation = "dummy_location"
    private val dummyBlog = "http://dummyblog.com"
    private val dummyName = "dummy_name"

    private lateinit var context: Context

    @Before
    fun iniTest() {

        context = ApplicationProvider.getApplicationContext()

        githubUserProfileViewModel = GithubUserProfileViewModel()

        startKoin {
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
    fun showGithubUserProfileTest() {

        val dummyProfile = getDummyGithubUserProfile(false)
        every { mockGithubUsersRepository.getGithubUserProfile(any()) } returns flowOf(dummyProfile)

        composeTestRule.setContent {
            GithubUserProfileScreen(
                navController = mockNavHostController,
                viewModel = githubUserProfileViewModel,
                login = dummyLogin
            )
        }

        composeTestRule.onNodeWithTag(PROFILE_NAME)
            .assert(hasText(dummyName))

        composeTestRule.onNodeWithTag(PROFILE_BIO)
            .assert(hasText(dummyBio))

        composeTestRule.onNodeWithTag(PROFILE_LOCATION)
            .assert(hasText(dummyLocation))

        composeTestRule.onNodeWithTag(PROFILE_LOGIN)
            .assert(hasText(dummyLogin))

        composeTestRule.onNodeWithTag(PROFILE_BLOG)
            .assert(hasText(dummyBlog))
    }

    @Test
    fun showGithubUserProfileIsAdminTest() {
        val dummyProfile = getDummyGithubUserProfile(true)
        every { mockGithubUsersRepository.getGithubUserProfile(any()) } returns flowOf(dummyProfile)

        composeTestRule.setContent {
            GithubUserProfileScreen(
                navController = mockNavHostController,
                viewModel = githubUserProfileViewModel,
                login = dummyLogin
            )
        }

        composeTestRule.onNodeWithTag(GITHUB_USER_STAFF)
            .assert(hasText(context.getString(R.string.text_staff)))
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(GITHUB_USER_STAFF_BACKGROUND)
            .assertIsDisplayed()
    }

    @Test
    fun showGithubUserProfileNoContentWithNoResponseTest() {
        every { mockGithubUsersRepository.getGithubUserProfile(any()) } returns emptyFlow()

        composeTestRule.setContent {
            GithubUserProfileScreen(
                navController = mockNavHostController,
                viewModel = githubUserProfileViewModel,
                login = dummyLogin
            )
        }

        composeTestRule.onNodeWithTag(PROFILE_NO_CONTENT)
            .assert(hasText(context.getString(R.string.text_no_content)))
    }

    @Test
    fun showGithubUserProfileNoContentWithLoginNullTest() {
        val dummyProfile = getDummyGithubUserProfile(true)
        every { mockGithubUsersRepository.getGithubUserProfile(any()) } returns flowOf(dummyProfile)

        composeTestRule.setContent {
            GithubUserProfileScreen(
                navController = mockNavHostController,
                viewModel = githubUserProfileViewModel,
                login = null
            )
        }

        composeTestRule.onNodeWithTag(PROFILE_NO_CONTENT)
            .assert(hasText(context.getString(R.string.text_no_content)))
    }

    @Test
    fun showGithubUserProfileWithNoBlog() {
        val dummyProfile = GitHubUserProfileEntity(
            dummyLogin,
            dummyAvatarUrl,
            dummyBio,
            null,
            dummyLocation,
            dummyName,
            false
        )
        every { mockGithubUsersRepository.getGithubUserProfile(any()) } returns flowOf(dummyProfile)

        composeTestRule.setContent {
            GithubUserProfileScreen(
                navController = mockNavHostController,
                viewModel = githubUserProfileViewModel,
                login = dummyLogin
            )
        }

        composeTestRule.onNodeWithTag(PROFILE_NO_BLOG)
            .assert(hasText(context.getString(R.string.text_no_blog)))
    }

    @Test
    fun closeGithubProfileTest() {

        val dummyProfile = GitHubUserProfileEntity(
            dummyLogin,
            dummyAvatarUrl,
            dummyBio,
            dummyBlog,
            dummyLocation,
            dummyName,
            false
        )
        every { mockGithubUsersRepository.getGithubUserProfile(any()) } returns flowOf(dummyProfile)

        composeTestRule.setContent {
            GithubUserProfileScreen(
                navController = mockNavHostController,
                viewModel = githubUserProfileViewModel,
                login = dummyLogin
            )
        }

        composeTestRule.onNodeWithTag(CLOSE_SCREEN)
            .performClick()

        verify { mockNavHostController.popBackStack() }
    }

    @After
    fun endTest() {
        stopKoin()
    }

    private fun getDummyGithubUserProfile(isAdmin: Boolean): GitHubUserProfileEntity {
        return GitHubUserProfileEntity(
            dummyLogin,
            dummyAvatarUrl,
            dummyBio,
            dummyBlog,
            dummyLocation,
            dummyName,
            isAdmin
        )
    }

}