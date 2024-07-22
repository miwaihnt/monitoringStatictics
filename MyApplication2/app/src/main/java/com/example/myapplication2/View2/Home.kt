package com.example.myapplication2.View2

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication2.ViewModel.FollowData
import androidx.compose.ui.unit.dp as dp1
import androidx.compose.foundation.clickable as clickable1

@Composable
fun Home(
    navController: NavController,
    FollowDataViewModel: FollowData = viewModel()
) {

    val iconList = listOf(
        Icons.Default.Add,
        Icons.Default.AccountBox,
        Icons.Default.AccountCircle,
        Icons.Default.AddCircle,
        Icons.Default.List,
        Icons.Default.ArrowBack,
        Icons.Default.ArrowDropDown,
        Icons.Default.ArrowForward,
        Icons.Default.Build,
        Icons.Default.Call,
        Icons.Default.Check,
        Icons.Default.CheckCircle,
        Icons.Default.Clear,
        Icons.Default.Close,
        Icons.Default.Create,
        Icons.Default.DateRange,
        Icons.Default.Delete,
        Icons.Default.Done,
        Icons.Default.Edit,
        Icons.Default.Email,
        Icons.Default.ExitToApp,
        Icons.Default.Face,
        Icons.Default.Favorite,
        Icons.Default.FavoriteBorder,
        Icons.Default.Home,
        Icons.Default.Info,
        Icons.Default.KeyboardArrowDown,
        Icons.Default.KeyboardArrowLeft,
        Icons.Default.KeyboardArrowRight,
        Icons.Default.KeyboardArrowUp,
        Icons.Default.LocationOn,
        Icons.Default.Lock,
        Icons.Default.MailOutline,
        Icons.Default.Menu,
        Icons.Default.MoreVert,
        Icons.Default.Notifications,
        Icons.Default.Person,
        Icons.Default.Phone,
        Icons.Default.Place,
        Icons.Default.PlayArrow,
        Icons.Default.Refresh,
        Icons.Default.Search,
        Icons.Default.Send,
        Icons.Default.Settings,
        Icons.Default.Share,
        Icons.Default.ShoppingCart,
        Icons.Default.Star,
        Icons.Default.ThumbUp,
        Icons.Default.Warning,
    )

    LazyColumn(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val userList = FollowDataViewModel.userList
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
                        .clickable1 {}

                ) {
                    Text(
                        text = user.userName,
                        modifier = Modifier
                            .padding(16.dp1)
                            .clickable1 {}
                    )
                }
            }
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        IconButton(
            onClick = {
                navController.navigate("ListFriends")
            },
            modifier = Modifier
                .size(48.dp)
                .padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Add",
            )
        }
        IconButton(
            onClick = {
                // Settingボタンが押されたときの処理
                navController.navigate("Profile")
            },
            modifier = Modifier
                .size(48.dp)
                .padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Add",
            )
        }
    }
}