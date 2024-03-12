package com.testing.authmeapptesting.ui.screen

import androidx.compose.foundation.Image
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
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.testing.authmeapptesting.R
import com.testing.authmeapptesting.constants.CommonTestTag.CLOSE_SCREEN
import com.testing.authmeapptesting.constants.NavigationConstants.SCREEN_USER_PROFILE
import com.testing.authmeapptesting.constants.SearchGithubUsersScreenTestTag.SEARCH_INPUT
import com.testing.authmeapptesting.constants.SearchGithubUsersScreenTestTag.SEARCH_LIST_USERS
import com.testing.authmeapptesting.viewmodel.SearchGithubUserViewModel
import com.testing.githubusersdk.data.locale.GithubUserCompleteEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@ExperimentalPagingApi
@Composable
fun SearchGithubUserScreen(
    navController: NavHostController,
    viewModel: SearchGithubUserViewModel
) {

    var text by remember { mutableStateOf("") }
    val searchQueryState = remember { MutableStateFlow("") }
    val composeScope = rememberCoroutineScope()

    val lazyPagingData: LazyPagingItems<GithubUserCompleteEntity> =
        viewModel.queryGithubUsers(searchQueryState.value ?: "").collectAsLazyPagingItems()


    Column {
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {

            OutlinedTextField(
                value = text,
                onValueChange = {
                    text = it
                    composeScope.launch {
                        searchQueryState.value = text
                        delay(300L)
                    }
                },
                label = {
                    Text("Enter your search name")
                },
                modifier = Modifier.testTag(SEARCH_INPUT),
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag(SEARCH_LIST_USERS)
        ) {

            items(count = lazyPagingData.itemCount) { index ->
                val item = lazyPagingData[index]

                item?.let { entity ->
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                            .clickable {
                                navController.navigate("${SCREEN_USER_PROFILE}/${entity.user.login}")
                            }
                            .testTag(entity.user.login)
                    ) {
                        Row {

                            CircleCropImage(
                                url = entity.user.avatarUrl ?: "",
                                contentDescription = entity.user.avatarUrl ?: ""
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    entity.user.login ?: "",
                                    modifier = Modifier.padding(start = 10.dp, top = 15.dp),
                                    style = TextStyle(fontSize = 16.sp)
                                )

                                if (entity.user.siteAdmin == true) {
                                    Box(modifier = Modifier.padding(start = 10.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .matchParentSize()
                                                .background(
                                                    color = Color(95, 112, 222),
                                                    shape = RoundedCornerShape(13.dp)
                                                )
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