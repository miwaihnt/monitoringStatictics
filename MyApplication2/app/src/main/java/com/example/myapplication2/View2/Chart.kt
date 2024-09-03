package com.example.myapplication2.View2

import android.graphics.Paint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun AppUsageChart(mainUsageData: List<AppUsageData>, otherUsageData: AppUsageData?) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedAppUsageData by remember { mutableStateOf<AppUsageData?>(null) }

    // グラフの最大値を計算
    val maxTime = mainUsageData.maxOfOrNull { it.totalTimeInForeground } ?: 1L
    val roundedMaxTime = roundToNearest3Hours(maxTime)

    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        item {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.hsl(300F, 0.1F, 0.95F))
                    .border(BorderStroke(2.dp, Color.Black)) // 枠線を追加
            ) {

                Spacer(modifier = Modifier.height(24.dp))

                // 円グラフを表示する部分
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    PieChart(
                        appUsageData = mainUsageData + listOfNotNull(otherUsageData),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        onSegmentClick = { data ->
                            selectedAppUsageData = data
                            showDialog = false
                        }
                    )

                    // 凡例を表示
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        mainUsageData.forEachIndexed { index, data ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(colors[index % colors.size])
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = data.packageName,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = formatTime(data.totalTimeInForeground),
                                        fontSize = 12.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                        otherUsageData?.let { data ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(colors[mainUsageData.size % colors.size])
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "その他",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = formatTime(data.totalTimeInForeground),
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        item {

            Spacer(modifier = Modifier.height(32.dp))

            // ここから枠線と背景色を適用
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Gray.copy(alpha = 0.15f)) // グレーの背景色
                    .border(BorderStroke(2.dp, Color.Black)) // 黒い枠線
            ) {
                Column(modifier = Modifier.padding(8.dp)) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "アプリ",
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

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                        ) {
                            val startX = size.width * 0.2f + 6.dp.toPx()
                            val endX = size.width - 10.dp.toPx()
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

                    // アプリごとの棒グラフを表示
                    mainUsageData.forEach { data ->
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
                                        selectedAppUsageData = data
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
                                    .clickable {
                                        selectedAppUsageData = data
                                        showDialog = true
                                    }
                            )
                        }
                    }

                    // その他のデータを表示
                    otherUsageData?.let { data ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "その他",
                                modifier = Modifier
                                    .weight(0.2f)
                                    .clickable {
                                        selectedAppUsageData = data
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
                                    .clickable {
                                        selectedAppUsageData = data
                                        showDialog = true
                                    }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp)) // 枠線の下にスペースを追加
        }
    }

    if (showDialog && selectedAppUsageData != null) {
        AppDetailDialog(
            appUsageData = selectedAppUsageData!!,
            onDismissRequest = {
                showDialog = false
                selectedAppUsageData = null
            }
        )
    }
}


@Composable
fun AppDetailDialog(
    appUsageData: AppUsageData,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("閉じる")
            }
        },
        title = {
            Text("詳細", style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Column {
                Text("アプリ名 : ${appUsageData.packageName}", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text("使用時間 : ${formatTime(appUsageData.totalTimeInForeground)}", style = MaterialTheme.typography.bodySmall)
            }
        }
    )
}


@Composable
fun BarChart(value: Long, maxValue: Long, modifier: Modifier = Modifier,) {
    val animatedValue by animateFloatAsState(
        targetValue = value.toFloat() / maxValue,
        animationSpec = tween(durationMillis = 1000), label = ""
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val barWidth = size.width * animatedValue

            drawRect(
                color = Color(0xFFFF9800), // オレンジ色
                topLeft = Offset(0f, 0f),
                size = Size(barWidth, size.height),
            )

        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PieChart(
    appUsageData: List<AppUsageData>,
    modifier: Modifier = Modifier,
    onSegmentClick: (AppUsageData) -> Unit
) {
    val totalUsageTime = appUsageData.sumOf { it.totalTimeInForeground }
    val sweepAngles = appUsageData.map { 360f * (it.totalTimeInForeground / totalUsageTime.toFloat()) }
    val percentages = appUsageData.map { ((it.totalTimeInForeground / totalUsageTime.toFloat()) * 100).roundToInt() }


    // 調整して100%にする
    val adjustedPercentages = percentages.toMutableList()
    val totalPercentage = percentages.sum()
    if (adjustedPercentages.isNotEmpty() && totalPercentage != 100) {
        adjustedPercentages[adjustedPercentages.size - 1] += (100 - totalPercentage)
    }

    val animatedSweepAngles = sweepAngles.map { angle ->
        animateFloatAsState(targetValue = angle, animationSpec = tween(durationMillis = 1000)).value
    }

    var canvasSize by remember { mutableStateOf(Size.Zero) }

    Box(modifier = modifier.pointerInput(Unit) {
        detectTapGestures { offset ->
            val x = offset.x - canvasSize.width / 2
            val y = offset.y - canvasSize.height / 2
            val touchAngle = (Math.toDegrees(Math.atan2(y.toDouble(), x.toDouble())) + 360) % 360

            var startAngle = -90f
            for ((index, sweepAngle) in animatedSweepAngles.withIndex()) {
                val endAngle = startAngle + sweepAngle
                if (touchAngle in startAngle..endAngle || (startAngle < 0 && touchAngle in (360 + startAngle)..360.0f) || (endAngle > 360 && touchAngle in 0.0..((endAngle - 360).toDouble()))) {
                    onSegmentClick(appUsageData[index])
                    break
                }
                startAngle = endAngle
            }
        }
    }) {
        Canvas(modifier = Modifier.fillMaxSize().onSizeChanged { size ->
            canvasSize = size.toSize()
        }) {
            var startAngle = -90f

            animatedSweepAngles.forEachIndexed { index, sweepAngle ->
                drawArc(
                    color = colors[index % colors.size],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true
                )

                val labelAngle = Math.toRadians((startAngle + sweepAngle / 2).toDouble()).toFloat()
                val radius = size.minDimension / 2 / 1.5f
                val x = size.width / 2 + cos(labelAngle) * radius
                val y = size.height / 2 + sin(labelAngle) * radius

                if (adjustedPercentages[index] > 10) {
                    drawContext.canvas.nativeCanvas.drawText(
                        "${adjustedPercentages[index]}%",
                        x,
                        y,
                        Paint().apply {
                            color = Color.White.toArgb()
                            textAlign = Paint.Align.CENTER
                            textSize = 40f
                        }
                    )
                }

                startAngle += sweepAngle
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .background(Color.White, shape = CircleShape)
                .padding(16.dp)
        ) {
            Text(text = formatTime(totalUsageTime))
        }
    }
}
