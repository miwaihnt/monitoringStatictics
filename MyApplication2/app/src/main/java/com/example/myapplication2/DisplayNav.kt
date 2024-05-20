package com.example.myapplication2

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager

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
            HomeIcon(navController = navController,dbInfoGet,dbAddFollowData)
        }
        composable( route = "FriendSearchScreen") {
            FriendSearchScreen(dbAddFollowData,navController = navController)
        }
        composable( route = "StatisticsInfo"){
            StatisticsInfo(navController = navController,getStatistics)
        }

    }
    Log.d("DisplayNav","Called DisplayNav")
}


