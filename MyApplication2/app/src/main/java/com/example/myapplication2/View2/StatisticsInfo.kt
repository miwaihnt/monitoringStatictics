package com.example.myapplication2.View2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication2.ViewModel.StatisticsViewModel


@Composable
fun StatisticsInfo(
    navController: NavController,
    viewModel: StatisticsViewModel = hiltViewModel(),
    userName: String?
)  {
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

// 画面上部の戻るボタンとユーザー名の表示
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary) // 背景色を追加（任意）
                    .padding(8.dp) // パディングを調整
            ) {
                IconButton(
                    onClick = { navController.navigate("Home") },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft, // 戻るアイコン
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(Modifier.width(16.dp)) // ボタンとテキストの間にスペースを追加

                Text(
                    text = "${userName} 's Usage", // ユーザーの名前を表示
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.align(Alignment.CenterVertically) // テキストを中央揃えに
                )
            }

            UsageStatisticsScreen(dailyStatistics)
        }
    }
}