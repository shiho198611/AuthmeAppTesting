package com.testing.authmeapptesting.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.testing.authmeapptesting.R
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
import com.testing.authmeapptesting.viewmodel.GithubUserProfileViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Composable
fun GithubUserProfileScreen(
    navController: NavHostController,
    viewModel: GithubUserProfileViewModel,
    login: String?
) {

    if (login == null) {
        NoContentSurface(navigator = navController)
    } else {
        val entity = viewModel.getGithubUserProfileDataFlow(login)
            .collectAsState(
                initial = null,
                CoroutineScope(Dispatchers.IO).coroutineContext
            ).value

        if (entity != null) {
            Column(modifier = Modifier.verticalScroll(state = rememberScrollState())) {
                Box(modifier = Modifier.padding(10.dp)) {
                    Image(painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "",
                        modifier = Modifier
                            .size(50.dp, 50.dp)
                            .clickable {
                                navController.popBackStack()
                            }
                            .testTag(CLOSE_SCREEN)
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircleCropImage(
                        url = entity.avatarUrl ?: "",
                        contentDescription = entity.avatarUrl ?: "",
                        modifier = Modifier
                            .size(150.dp)
                            .padding(10.dp)
                    )

                    Text(
                        text = entity.name ?: "",
                        style = TextStyle(fontSize = 18.sp),
                        modifier = Modifier.testTag(PROFILE_NAME)
                    )

                    Box(modifier = Modifier.padding(5.dp)) {
                        Text(
                            text = entity.bio ?: "",
                            style = TextStyle(fontSize = 16.sp),
                            modifier = Modifier.testTag(PROFILE_BIO)
                        )
                    }

                    Box(modifier = Modifier.padding(vertical = 10.dp)) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 15.dp)
                                .height(1.dp)
                                .fillMaxWidth()
                                .background(
                                    color = Color.Black,
                                    shape = RectangleShape
                                )
                        )
                    }
                }

                Row(modifier = Modifier.padding(start = 15.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_account_circle),
                        contentDescription = "",
                        modifier = Modifier.size(50.dp, 50.dp)
                    )

                    Column(modifier = Modifier.padding(start = 10.dp)) {
                        Text(
                            text = entity.login,
                            style = TextStyle(fontSize = 16.sp, color = Color.Gray),
                            modifier = Modifier.testTag(PROFILE_LOGIN)
                        )

                        if (entity.siteAdmin == true) {
                            Box {
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(
                                            color = Color(95, 112, 222),
                                            shape = RoundedCornerShape(13.dp)
                                        )
                                        .testTag(GITHUB_USER_STAFF_BACKGROUND)
                                )

                                Text(
                                    text = stringResource(id = R.string.text_staff),
                                    style = TextStyle(fontSize = 16.sp, color = Color.White),
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .testTag(GITHUB_USER_STAFF)
                                )
                            }
                        }

                    }
                }

                Row(modifier = Modifier.padding(start = 15.dp, top = 20.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_location),
                        contentDescription = "",
                        modifier = Modifier.size(50.dp, 50.dp)
                    )

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        Text(
                            text = entity.location
                                ?: stringResource(id = R.string.text_no_location),
                            style = TextStyle(fontSize = 16.sp, color = Color.Gray),
                            modifier = Modifier.testTag(PROFILE_LOCATION)
                        )
                    }
                }

                Row(modifier = Modifier.padding(start = 15.dp, top = 20.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_link),
                        contentDescription = "",
                        modifier = Modifier.size(50.dp, 50.dp)
                    )

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .align(Alignment.CenterVertically)
                    ) {

                        if (entity.blog.isNullOrEmpty()) {
                            Text(
                                text = stringResource(id = R.string.text_no_blog),
                                style = TextStyle(fontSize = 16.sp, color = Color.Gray),
                                modifier = Modifier.testTag(PROFILE_NO_BLOG)
                            )
                        } else {
                            HyperlinkText(entity.blog!!)
                        }

                    }
                }
            }

        } else {
            NoContentSurface(navigator = navController)
        }

    }


}

@Composable
fun HyperlinkText(url: String) {

    val uriHandler = LocalUriHandler.current

    val text = AnnotatedString.Builder().apply {
        pushStringAnnotation(tag = "URL", annotation = url)
        append(url)
        pop()
    }.toAnnotatedString()

    ClickableText(
        text = text,
        onClick = { offset ->
            text.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    uriHandler.openUri(annotation.item)
                }
        }, style = TextStyle(
            textDecoration = TextDecoration.Underline,
            color = Color(0xff64B5F6)
        ), modifier = Modifier
            .testTag(PROFILE_BLOG)
    )
}

@Composable
fun NoContentSurface(navigator: NavHostController) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column {
            Box(modifier = Modifier.padding(10.dp)) {
                Image(painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "",
                    modifier = Modifier
                        .size(50.dp, 50.dp)
                        .clickable {
                            navigator.popBackStack()
                        }
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.text_no_content),
                    style = TextStyle(fontSize = 24.sp),
                    modifier = Modifier.testTag(PROFILE_NO_CONTENT)
                )
            }
        }

    }
}