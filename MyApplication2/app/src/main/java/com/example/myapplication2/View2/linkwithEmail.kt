package com.example.myapplication2.View2

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication2.ViewModel.GoogleAuthViewModel

@Composable
fun linkWithEmail(
    GoogleAuthViewModel: GoogleAuthViewModel = viewModel(),
    navController: NavController
    ) {

    var userName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var passoword by remember { mutableStateOf("") }



    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        Text(
            text = "【セキュリティ強化】",
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
        )

        Text(
            text = "基本情報を設定",
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
        )

        Spacer(modifier = Modifier.padding(16.dp))

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
            GoogleAuthViewModel.linkWithEmailPassoword(userName,email,passoword,navController)
            Log.d("linkWithEmail","userName:$userName,email:$email,password:$passoword")
        },
            modifier = Modifier.padding(16.dp),
        ) {
            Text(text = "新規登録")
        }

    }



}