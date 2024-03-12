package com.testing.authmeapptesting.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.testing.authmeapptesting.R
import com.testing.authmeapptesting.constants.CommonTestTag.GITHUB_USER_STAFF_BACKGROUND
import com.testing.authmeapptesting.constants.ListGithubUsersScreenTestTag.LIST_GITHUB_USERS
import com.testing.authmeapptesting.constants.ListGithubUsersScreenTestTag.SEARCH_BUTTON
import com.testing.authmeapptesting.constants.NavigationConstants.SCREEN_SEARCH_USERS
import com.testing.authmeapptesting.constants.NavigationConstants.SCREEN_USER_PROFILE
import com.testing.authmeapptesting.viewmodel.ListGithubUsersViewModel
import com.testing.githubusersdk.data.locale.GitHubUserEntity

@ExperimentalPagingApi
@Composable
fun ListGithubUsersScreen(
    navController: NavHostController,
    viewModel: ListGithubUsersViewModel
) {

    val lazyPagingData: LazyPagingItems<GitHubUserEntity> =
        viewModel.queryGithubUsers().collectAsLazyPagingItems()

    Column {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            Button(
                onClick = {
                    navController.navigate(SCREEN_SEARCH_USERS)
                }, modifier = Modifier
                    .fillMaxWidth()
                    .testTag(SEARCH_BUTTON)
            ) {
                Text(
                    text = stringResource(id = R.string.text_search),
                    style = TextStyle(fontSize = 16.sp)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag(LIST_GITHUB_USERS)
        ) {

            items(count = lazyPagingData.itemCount) { index ->
                val item = lazyPagingData[index]

                item?.let { entity ->
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                            .clickable {
                                navController.navigate("${SCREEN_USER_PROFILE}/${entity.login}")
                            }
                            .testTag(entity.login)
                    ) {
                        Row {

                            CircleCropImage(
                                url = entity.avatarUrl ?: "",
                                contentDescription = entity.avatarUrl ?: "",
                                modifier = Modifier
                                    .size(80.dp)
                                    .padding(10.dp)
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    entity.login ?: "",
                                    modifier = Modifier.padding(start = 10.dp, top = 15.dp),
                                    style = TextStyle(fontSize = 16.sp)
                                )

                                if (entity.siteAdmin == true) {
                                    Box(modifier = Modifier.padding(start = 10.dp)) {
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
                                            style = TextStyle(
                                                fontSize = 16.sp,
                                                color = Color.White
                                            ),
                                            modifier = Modifier.padding(4.dp)
                                        )
                                    }

                                }
                            }

                        }
                    }
                }

            }

        }
    }


}
