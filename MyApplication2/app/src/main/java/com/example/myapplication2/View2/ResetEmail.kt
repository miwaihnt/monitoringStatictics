package com.example.myapplication2.View2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication2.ViewModel.LoginViewModel
import com.example.myapplication2.ViewModel.ResetEmailPasswordViewModel


@Composable
fun ResetEmailPassword(
    ResetEmailPassword: ResetEmailPasswordViewModel = viewModel(),
    navController:NavController
    ) {

    var Email by remember{ mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Text(
            text = "パスワード再設定を行います。",
            modifier = Modifier.padding(8.dp),
            fontSize = 16.sp
        )
        Text(
            text = "アカウントのメールアドレスを入力してください",
            modifier = Modifier.padding(8.dp),
            fontSize = 16.sp
        )
        Text(
            text = "入力アドレス宛に再設定リンクが送信されます",
            modifier = Modifier.padding(8.dp),
            fontSize = 16.sp
        )

        TextField(
            value = Email,
            onValueChange = { Email = it },
            modifier = Modifier
                .padding(16.dp)
        )

        Button(
            onClick = {
                ResetEmailPassword.resetEmailPassword(Email,navController)

        }) {
            Text(text = "送信")
        }

    }



}
    
