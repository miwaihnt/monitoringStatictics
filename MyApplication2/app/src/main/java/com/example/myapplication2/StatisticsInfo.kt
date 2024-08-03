package com.example.myapplication2

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun StatisticsInfo(navController: NavController, getStatistics: getStatistics) {
    var dailyStatistics by remember { mutableStateOf(emptyList<DailyStatistics>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val currentUser = FirebaseAuth.getInstance().currentUser

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
// 統計取得ボタンの表示を条件付きにする
        if (dailyStatistics.isEmpty() && errorMessage == null) {
            Button(onClick = {
                if (currentUser != null) {
                    getStatistics.getStatisticsInfo(currentUser.uid) { data ->
                        dailyStatistics = data
                        errorMessage = if (data.isEmpty()) "No data found" else null
                    }
                } else {
                    errorMessage = "User is not logged in"
                }
            }) {
                Text(text = "統計取得")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (errorMessage != null) {
            Text(text = "Error: $errorMessage", color = Color.Red)
        } else if (dailyStatistics.isNotEmpty()) {
            UsageStatisticsScreen(dailyStatistics)
        }
    }
}