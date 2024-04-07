package com.example.myapplication2

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.util.Log
import com.google.firebase.auth.FirebaseAuth

@Composable
fun DisplayNav(userRegistration: UserRegistration,auth: FirebaseAuth) {

    val navController = rememberNavController()

    NavHost(navController = navController,
            startDestination = "Login" ) {

        composable( route = "RegistrationScreen") {
            RegistrationScreen(navController = navController,userRegistration)
        }
        composable(route = "Login") {
            Login(navController = navController,auth)
        }
        composable( route = "HomeIcon") {
            HomeIcon(navController = navController)
        }
    }
    Log.d("DisplayNav","Called DisplayNav")
}