package com.example.myapplication2.View2

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.myapplication2.R
import com.example.myapplication2.ViewModel.CurrentUserViewModel
import com.example.myapplication2.ViewModel.SearchUserViewModel

@Composable
fun SearchName(
    SearchUserViewModel: SearchUserViewModel = viewModel(),
) {
    
    val user by SearchUserViewModel.user.collectAsState()
    var inputId by remember { mutableStateOf("") }
    var searchFlag by rememberSaveable { mutableStateOf(false) }

    Box (
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Black),
        contentAlignment = Alignment.TopCenter

    ){

        Column(
            modifier = Modifier
                .fillMaxWidth()
        )
        {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically

            )
            {
                TextField(
                    value = inputId,
                    onValueChange = {inputId = it} ,
                    label = { Text("ユーザID") },
                    modifier = Modifier
                        .padding(8.dp),

                    )

                Button(
                    onClick = {
                        //データベースにアクセスし、ユーザ情報を取得する
                        SearchUserViewModel.fetchReqUser(inputId)
                        searchFlag = true
                    }
                ) {
                    Text(text = "検索")
                }
            }

            if (searchFlag){
                Card(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .padding(8.dp)
                        .clip(CardDefaults.shape),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),

                    ) {
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically

                    ){
                        AsyncImage(
                            model = user.profileImage, contentDescription = "",
                            modifier = Modifier
                                .height(96.dp)
                                .width(96.dp)
                                .padding(8.dp)
                        )
                        Text(
                            text = user.userName,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        IconButton(
                            modifier = Modifier
                                .padding(36.dp)
                                .background(color = Color.White)
                                .size(48.dp),
                            onClick = {
                                SearchUserViewModel.updateFollow()
                                SearchUserViewModel.followReqUser()
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add, // プラスアイコン
                                contentDescription = "Add Friend",
                                tint = Color.Black,
                            )
                        }
                    }
                }

            }


        }


    }


}