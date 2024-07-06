package com.example.myapplication2

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
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


fun formatTime(milliseconds: Long): String {
    val totalMinutes = milliseconds / 1000 / 60
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return String.format("%02d 時間 %02d 分", hours, minutes)
}

@Composable
fun roundToNearest6Hours(milliseconds: Long): Long {
    val hours = milliseconds / 1000 / 60 / 60
    return when {
        hours <= 3 -> 3 * 60 * 60 * 1000
        hours <= 6 -> 6 * 60 * 60 * 1000
        hours <= 9 -> 9 * 60 * 60 * 1000
        hours <= 12 -> 12 * 60 * 60 * 1000
        hours <= 15 -> 15 * 60 * 60 * 1000
        hours <= 18 -> 18 * 60 * 60 * 1000
        hours <= 21 -> 21 * 60 * 60 * 1000
        else -> 24 * 60 * 60 * 1000
    }
}

@Composable
fun AppUsageChart(appUsageData: List<AppUsageData>) {
    var showDialog by remember { mutableStateOf(false) }
    var dialogText by remember { mutableStateOf("") }

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
            Text(
                text = "アプリ名",
                modifier = Modifier.weight(0.2f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "使用時間",
                modifier = Modifier.weight(0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        val maxTime = appUsageData.maxOfOrNull { it.totalTimeInForeground } ?: 1L
        val roundedMaxTime = roundToNearest6Hours(maxTime)

        Box(modifier = Modifier.fillMaxWidth()) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)  // 追加されたスペース
            ) {
                val startX = size.width * 0.2f + 4.dp.toPx()
                val endX = size.width
                val segmentWidth = (endX - startX) / 3

                drawLine(
                    color = Color.Black,
                    start = Offset(startX, size.height / 2),
                    end = Offset(endX, size.height / 2)
                )

                for (i in 0..3) {
                    val x = startX + segmentWidth * i
                    drawLine(
                        color = Color.Gray,
                        start = Offset(x, size.height / 2 - 10.dp.toPx()),
                        end = Offset(x, size.height / 2 + 10.dp.toPx())
                    )
                    drawContext.canvas.nativeCanvas.drawText(
                        (i * roundedMaxTime / (3 * 60 * 60 * 1000)).toString(),
                        x,
                        size.height / 2 + 30f,
                        Paint().apply {
                            color = Color.Black.toArgb()
                            textSize = 30f
                        }
                    )
                }
            }
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
                        modifier = Modifier
                            .weight(0.2f)
                            .clickable {
                                dialogText = "${data.packageName}\n使用時間 : ${formatTime(data.totalTimeInForeground)}"
                                showDialog = true
                            },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    BarChart(
                        value = data.totalTimeInForeground,
                        maxValue = roundedMaxTime,
                        modifier = Modifier
                            .weight(0.8f)
                            .height(24.dp)
                    )
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("OK")
                    }
                },
                text = { Text(dialogText) }
            )
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
                val widthWithPadding = barWidth
                canvas.nativeCanvas.drawRect(0f, 0f, widthWithPadding, size.height, paint)
            }
        }
    }
}