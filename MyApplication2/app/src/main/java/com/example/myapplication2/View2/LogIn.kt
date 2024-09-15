package com.example.myapplication2.View2

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication2.ViewModel.CurrentUserViewModel
import com.example.myapplication2.ViewModel.FileUploadViewModel
import com.example.myapplication2.ViewModel.LoginViewModel
import com.example.myapplication2.ViewModel.UploadStatisticsViewModel
import com.example.myapplication2.ViewModel.UsageStatsViewModel

@Composable
fun LogInView(
    loginViewModel: LoginViewModel = viewModel(),
    currentUserViewModel: CurrentUserViewModel = viewModel(),
    navController: NavController
) {

    val usageStatsViewModel: UsageStatsViewModel = hiltViewModel()
    val statisticsViewModel: UploadStatisticsViewModel = hiltViewModel()
    val fileUploadViewModel: FileUploadViewModel = hiltViewModel()


    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.padding(16.dp)
        )

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.padding(16.dp)
        )

        Button(
            onClick = {
                loginViewModel.logIn(
                    email,
                    password,
                    navController,
                    usageStatsViewModel, // UsageStatsViewModel を渡す
                    statisticsViewModel, // UploadStatisticsViewModel を渡す
                    fileUploadViewModel // FileUploadViewModel を渡す
                )
            },
            modifier = Modifier
                .padding(16.dp)
                .width(200.dp)
        ) {
            Text(text = "Login")
        }
        Button(
            onClick = {
                navController.navigate("UserRegistrate")
            },
            modifier = Modifier
                .padding(16.dp)
                .width(200.dp)
        ) {
            Text(text = "登録")
        }

    }

}
