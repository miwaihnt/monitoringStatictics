package com.example.myapplication2.View2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication2.ViewModel.UserResistrationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserRegistrate(
    navController: NavController,
    UserResistrationViewModel: UserResistrationViewModel = viewModel()
) {
    var userName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var passoword by remember { mutableStateOf("") }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "戻る") }, // タイトルを表示
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("LogInView") }) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft, // ＜ のアイコン
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
            )
        }
    )

    { PaddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize()
                .padding(PaddingValues)

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
                UserResistrationViewModel.AuthUserRegistration(userName,email,passoword, navController)
                },
                modifier = Modifier.padding(16.dp),
            ) {
                Text(text = "新規登録")
            }
        }
    }

    if (UserResistrationViewModel.alertDialog.value) {
        AlertDialog(
            onDismissRequest = {
                UserResistrationViewModel.alertDialog.value = false
            },
            confirmButton =  { Button(onClick = { UserResistrationViewModel.alertDialog.value = false }) 
                {
                    Text(text = "OK")
                }
            },
            title =  {Text(text = "入力エラー")
            },
            text = {
                Text(text = UserResistrationViewModel.alertDialogMessage.value)
            }
        )

    }
}