package com.example.myapplication2.View2

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication2.ViewModel.FileUploadViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.myapplication2.ViewModel.CurrentUserViewModel
import com.example.myapplication2.ViewModel.FollowData
import com.example.myapplication2.ViewModel.FollowReqUserViewModel
import com.example.myapplication2.ViewModel.GoogleAuthViewModel
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
            val GoogleAuthViewModel = hiltViewModel<GoogleAuthViewModel>()
            LogInView(
                navController = navController,
                loginViewModel =  loginViewModel,
                currentUserViewModel = currenUserViewModel,
                GoogleAuthViewModel = GoogleAuthViewModel
            )
        }


        composable("UserRegistrate") {
            val UserResistrationViewModel = hiltViewModel<UserResistrationViewModel>()
            UserRegistrate(navController = navController,UserResistrationViewModel = UserResistrationViewModel)
        }

        composable("linkWithEmail") {
            val GoogleAuthViewModel = hiltViewModel<GoogleAuthViewModel>()

            linkWithEmail(
                GoogleAuthViewModel = GoogleAuthViewModel,
                navController = navController,
                )
        }


//        composable(
//            "UserRegistrate/{uid}",
//            arguments = listOf(navArgument("uid"){type= NavType.StringType})
//
//        ) { backstackEntry->
//            val uid = backstackEntry.arguments?.getString("uid")
//            if (uid !==null) {
//                val UserResistrationViewModel = hiltViewModel<UserResistrationViewModel>()
//                UserRegistrate(navController = navController,UserResistrationViewModel = UserResistrationViewModel,uid)
//            } else {
//                Log.e("NavGraph", "UID is null, navigating back")
//            }
//        }

        composable("profile") {
            val curretUserViewModel = hiltViewModel<CurrentUserViewModel>()
            val fileUploadViewModel = hiltViewModel<FileUploadViewModel>()

            Profile(currentUserViewModel = curretUserViewModel,fileUploadViewModel = fileUploadViewModel,navController = navController)

        }

        composable(route = "FileUploadScreen") {
            FileUploadScreen()
        }

        composable(route = "SearchName") {
            val SearchUserViewModel = hiltViewModel<SearchUserViewModel>()
            SearchName(navController = navController,SearchUserViewModel = SearchUserViewModel)
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

        composable("StatisticsInfo/{userId},{userName}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            val userName = backStackEntry.arguments?.getString("userName")
            val viewModel: StatisticsViewModel = hiltViewModel()
            LaunchedEffect(userId) {
                if (userId != null) {
                    viewModel.getUserStatistics(userId)
                }
            }
            StatisticsInfo(navController = navController, viewModel = viewModel, userName = userName)
        }

    }
    Log.d("DisplayGraph", "Called DisplayNav")
}
