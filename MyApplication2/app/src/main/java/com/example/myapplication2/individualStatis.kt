package com.example.myapplication2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun IndividualStatis(navController: NavController) {

    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.Center,
    )

    {
        Text(text = "統計情報用のグラフ")
    }


}