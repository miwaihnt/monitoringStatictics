package com.example.myapplication2

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// データクラス
data class AppUsageData(
    val packageName: String,
    val totalTimeInForeground: Long
)

data class DailyStatistics(
    val date: String,
    val apps: List<AppUsageData>
)

@Composable
fun AppUsageTable(appUsageData: List<AppUsageData>) {
    val maxTime = appUsageData.maxOfOrNull { it.totalTimeInForeground } ?: 1L

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = "アプリ名", modifier = Modifier.weight(1f))
            Text(text = "使用時間 (ms)", modifier = Modifier.weight(1f))
        }
        LazyColumn {
            items(appUsageData) { data ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = data.packageName,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    BarChart(
                        value = data.totalTimeInForeground,
                        maxValue = maxTime,
                        modifier = Modifier
                            .weight(1f)
                            .height(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BarChart(value: Long, maxValue: Long, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawIntoCanvas { canvas ->
                val barWidth = size.width * (value.toFloat() / maxValue.toFloat())
                val paint = Paint().apply {
                    color = Color.Blue.toArgb()
                }
                // 右側に30%の空白を追加
                val widthWithPadding = barWidth * 0.7f
                canvas.nativeCanvas.drawRect(0f, 0f, widthWithPadding, size.height, paint)
            }
        }
        Text(
            text = value.toString(),
            fontSize = 10.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 4.dp, bottom = 4.dp)
        )
    }
}
