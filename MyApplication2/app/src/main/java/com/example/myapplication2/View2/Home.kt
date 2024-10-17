package com.example.myapplication2.View2

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication2.Data.AllUser
import com.example.myapplication2.ViewModel.CurrentUserViewModel
import com.example.myapplication2.ViewModel.FollowData
//import com.example.myapplication2.ViewModel.Pokemon
import com.example.myapplication2.ViewModel.SearchBarEvent
//import com.example.myapplication2.ViewModel.SerchBarViewModel
import com.example.myapplication2.ViewModel.UiState
import androidx.compose.ui.unit.dp as dp1
import androidx.compose.foundation.clickable as clickable1



//例　検索UI
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    FollowDataViewModel: FollowData = viewModel(),
    navController: NavController
) {
    val uiState by FollowDataViewModel._uiState.collectAsState()
    val listLazyListState = rememberLazyListState()

    Box(modifier = Modifier
        .fillMaxWidth()
        .background(color = Color.Black)) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(16.dp)
        ) {
            SearchBarContent(
                modifier = Modifier
                    .padding(16.dp)
                    .height(8.dp),
                UiState = uiState,
                onEvent = FollowDataViewModel::onEvent

            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                state = listLazyListState,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                val userList = uiState.userList
                items(userList) { user ->
                    UserListItem(user = user, navController = navController)
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
                navController.navigate("FollowUser")
            },
            modifier = Modifier
                .size(48.dp)
                .padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = Color.White
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
                tint = Color.White
            )
        }
    }
}
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SearchBarContent(
        modifier: Modifier,
        UiState: UiState,
        onEvent: (SearchBarEvent) -> Unit,
    ){
        var active by rememberSaveable { mutableStateOf(false) }
        val query = UiState.query
        val isQuering = UiState.isQuerying
        val result: List<AllUser> = if (isQuering) {
            UiState.userList
        } else {
            UiState.searchHistory
        }

        DockedSearchBar(
            query = query,
            onQueryChange = {
                onEvent(SearchBarEvent.QueryChange(it))
            },
            onSearch = {active = false},
            active = active,
            onActiveChange = {active = it},
            shape = RoundedCornerShape(0.dp),
            leadingIcon = {
                if (active) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clickable {
                                onEvent(SearchBarEvent.Back)
                                active = false
                            },
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "",
                        modifier = Modifier.padding(start = 16.dp),
                    )
                }
            },
            trailingIcon = {
                if (isQuering) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(12.dp)
                            .size(32.dp)
                            .clickable {
                                onEvent(SearchBarEvent.Cancel)
                                active = false
                            },
                    )
                }
            },

            ) {
            if (result.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ){
                    items(items = result) {
                        Log.d("Home","itemname $it.name")
                        // Search result
                        ListItem(
                            headlineContent = { Text(text = it.userName) },
                            leadingContent = {
                                if(UiState.isQuerying) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "",
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "",
                                    )
                                }
                            },
                            modifier = Modifier.clickable {
                                onEvent(SearchBarEvent.Select(it))
                                active = false
                            }
                        )
                    }
                }
            } else {

            }
        }
    }



    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun UserListItem(user: AllUser, navController: NavController) {
        Card(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .clip(CardDefaults.shape)
                .combinedClickable(
                    onClick = { // "StatisticsInfo"画面に遷移
                        navController.navigate("StatisticsInfo/${user.docId},${user.userName}")
                        Log.d("tapped userName", "${user.userName},${user.docId}")
                    },
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
                Row ( modifier = Modifier.fillMaxWidth()){

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
                }
            }

        }
    }



