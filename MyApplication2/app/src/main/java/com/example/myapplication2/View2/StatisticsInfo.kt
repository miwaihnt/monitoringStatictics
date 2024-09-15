package com.example.myapplication2.View2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication2.ViewModel.StatisticsViewModel


@Composable
fun StatisticsInfo(navController: NavController, viewModel: StatisticsViewModel = hiltViewModel()) {
    val dailyStatistics by viewModel.dailyStatistics.observeAsState(emptyList())
    val errorMessage by viewModel.errorMessage.observeAsState(null)

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        if (errorMessage != null) {
            Text(text = "Error: $errorMessage", color = Color.Red)
        } else if (dailyStatistics.isNotEmpty()) {
            UsageStatisticsScreen(dailyStatistics)
        }
    }
}