package com.example.myapplication2.View2

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication2.Data.AllUser
import com.example.myapplication2.ViewModel.FollowReqUserViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.Alignment


@Composable

fun FollowUser(
    navController: NavController,
    FollowReqUserViewModel: FollowReqUserViewModel = viewModel(),
) {

    val listLazyListState = rememberLazyListState()

    //userInfoの取得
    LaunchedEffect(Unit) {
        FollowReqUserViewModel.fetchFollowReqUserList()
    }


//User検索のUIを定義
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(color = Color.Black)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = "友達を探す",
                modifier = Modifier
                    .padding(bottom = 16.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
                )
            //検索バーの実装
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Spacer(modifier = Modifier.weight(1f))
                Column()
                {
                    IconButton(
                        onClick = { navController.navigate("SearchName") },
                        modifier = Modifier
                            .padding(8.dp)
                            .size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Serach",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Text(
                        text = "名前検索",
                        color = Color.White,
                    )
                }

                Spacer(modifier = Modifier.weight(2f))
                Column(

                ) {
                    IconButton(
                        onClick = { FollowReqUserViewModel.fetchFollowReqUserList() },
                        modifier = Modifier
                            .padding(8.dp)
                            .size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Autorenew,
                            contentDescription = "Serach",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Text(
                        text = "画面更新",
                        color = Color.White,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }

            // 境界線を追加
            Divider(
                color = Color.White.copy(alpha = 0.3f),
                thickness = 1.dp,
            )


            Text(
                modifier = Modifier
                    .padding(16.dp),
                text = "友達かも？？",
                color = Color.White
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                state = listLazyListState,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val userList: List<AllUser> = FollowReqUserViewModel.userList.toList()
                Log.d("FollowUser","userList:$userList")

                items(userList) { user ->
                    FolowReqUserList(user = user, viewModel = FollowReqUserViewModel)
                }
            }
        }
        }

    }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FolowReqUserList(
    user: AllUser,
    viewModel: FollowReqUserViewModel
) {
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .clip(CardDefaults.shape)
            .combinedClickable(
                onClick = { /* to go detail screen */ },
                onLongClick = { /* to selected */ }
            )
            .clip(CardDefaults.shape),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .height(48.dp)
        ){
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically

                ){

                    AsyncImage(model = user.profileImage,
                        contentDescription = "" ,
                        modifier = Modifier
                            .height(48.dp)
                            .width(48.dp)
                    )
                    Text(
                        text = user.userName,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .background(color = Color.White)
                            .size(36.dp),
                        onClick = { viewModel.followReqUser(user.docId) },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Friend",
                            modifier = Modifier
                                .size(36.dp),
                        )
                    }
                }
            }
        }

    }

