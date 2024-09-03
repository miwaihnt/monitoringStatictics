package com.example.myapplication2.View2

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication2.ViewModel.FileUploadViewModel
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication2.StatisticsInfo
import com.example.myapplication2.ViewModel.CurrentUserViewModel
import com.example.myapplication2.ViewModel.FollowData
import com.example.myapplication2.ViewModel.FollowReqUserViewModel
import com.example.myapplication2.ViewModel.LoginViewModel
import com.example.myapplication2.ViewModel.SearchUserViewModel
import com.example.myapplication2.ViewModel.StatisticsViewModel
//import com.example.myapplication2.ViewModel.SerchBarViewModel
import com.example.myapplication2.ViewModel.UserResistrationViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    Log.d("NavGraph", "NavGraph called")

    NavHost(navController = navController, startDestination = "LogInView") {

        composable("LogInView") {
            val loginViewModel = hiltViewModel<LoginViewModel>()
            val currenUserViewModel = hiltViewModel<CurrentUserViewModel>()
            LogInView(navController = navController, loginViewModel =  loginViewModel,currentUserViewModel = currenUserViewModel)
        }

        composable("UserRegistrate") {
            val UserResistrationViewModel = hiltViewModel<UserResistrationViewModel>()
            UserRegistrate(navController = navController,UserResistrationViewModel = UserResistrationViewModel)
        }

        composable("profile") {
            val curretUserViewModel = hiltViewModel<CurrentUserViewModel>()
            val fileUploadViewModel = hiltViewModel<FileUploadViewModel>()

            Profile(currentUserViewModel = curretUserViewModel,fileUploadViewModel = fileUploadViewModel)

        }

        composable(route = "FileUploadScreen") {
            FileUploadScreen()
        }

        composable(route = "SearchName") {
            val SearchUserViewModel = hiltViewModel<SearchUserViewModel>()
            SearchName(SearchUserViewModel = SearchUserViewModel)
        }

        composable(route = "FollowUser") {
            val FollowReqUserViewModel = hiltViewModel<FollowReqUserViewModel>()
            FollowUser(navController = navController,FollowReqUserViewModel = FollowReqUserViewModel)
        }

        composable(route = "Home") {
            val FollowDataViewModel = hiltViewModel<FollowData>()
//            val SerchBarViewModel = hiltViewModel<SerchBarViewModel>()
            Home(navController = navController,FollowDataViewModel = FollowDataViewModel)
//            Home(SerchBarViewModel = SerchBarViewModel)
        }
        composable(route = "ListFriendsUI") {
            ListFriendsUI()
        }

        composable("StatisticsInfo/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            val viewModel: StatisticsViewModel = hiltViewModel()
            LaunchedEffect(userId) {
                if (userId != null) {
                    viewModel.getUserStatistics(userId)
                }
            }
            StatisticsInfo(navController = navController, viewModel = viewModel)
        }

    }
    Log.d("DisplayGraph", "Called DisplayNav")
}
