package com.example.myapplication2

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.app.usage.UsageStats
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication2.ViewModel.StatisticsViewModel

@Composable
fun DisplayNav(userRegistration: UserRegistration, auth: FirebaseAuth, dbInfoGet: dbInfoGet,
               dbAddFollowData: dbAddFollowData,FirebaseFirestore: FirebaseFirestore,usageStats:List<UsageStats>,getStatistics:getStatistics) {

    val navController = rememberNavController()


    NavHost(navController = navController,
        startDestination = "Login" ) {

        composable( route = "RegistrationScreen") {
            RegistrationScreen(navController = navController,userRegistration,auth)
        }
        composable(route = "Login") {
            Login(navController = navController,auth,FirebaseFirestore,usageStats)
        }
        composable( route = "HomeIcon") {
            HomeIcon(navController = navController,dbInfoGet,dbAddFollowData,auth)
        }
        composable( route = "FriendSearchScreen") {
            FriendSearchScreen(dbAddFollowData,navController = navController)
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
        composable( route = "ProfileSettingsScreen") {
            ParentComponent(navController = navController)
        }
        composable( route = "ListFriends") {
            ListFriends(navController= navController,dbInfoGet,auth)
        }
        composable( route = "IndividualStatis") {
            IndividualStatis(navController = navController)
        }

    }
    Log.d("DisplayNav","Called DisplayNav")
}


