package com.example.myapplication2.View2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication2.ViewModel.UserResistrationViewModel

@Composable
fun UserRegistrate(
    navController: NavController,
    UserResistrationViewModel: UserResistrationViewModel = viewModel()
) {
    var userName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var passoword by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        TextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("ユーザ名") },
            modifier = Modifier.padding(16.dp)
        )

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.padding(16.dp)
        )

        TextField(
            value = passoword,
            onValueChange = { passoword = it },
            label = { Text("Password") },
            modifier = Modifier.padding(16.dp)
        )

        Button(onClick = {
            UserResistrationViewModel.AuthUserRegistration(userName,email,passoword)
            navController.navigate("LogInView")
        }) {
            Text(text = "新規登録")
        }

    }
}