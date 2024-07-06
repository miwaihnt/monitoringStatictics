package com.example.myapplication2.View2

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication2.ViewModel.FollowData
import androidx.compose.foundation.clickable as clickable1
import androidx.compose.ui.unit.dp as dp1
import androidx.compose.foundation.lazy.items


@Composable
fun ListFriendsUI(viewModel: FollowData = viewModel()) {

    LazyColumn(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val userList = viewModel.userList
        Log.d("ListFriends","userList:$userList")
        items(userList) { user ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp1)
                    .clickable1 { },

                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp1),
                shape = MaterialTheme.shapes.medium // カードの形状を設定

            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp1)
                        .clickable1 { ontapped() }

                ) {
                    Text(
                        text = user.userName,
                        modifier = Modifier
                            .padding(16.dp1)
                            .clickable1 { ontapped() }
                    )
                }
            }
        }
    }
}

private fun ontapped() {
//    navController.navigate("StatisticsInfo")

}