package com.example.myapplication2

import android.graphics.Paint
import android.view.MotionEvent
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import okio.blackholeSink
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin


// カスタムカラーの定義
val colors = listOf(
    Color(0xFFBB86FC), // Light Purple
    Color(0xFF6200EE), // Purple
    Color(0xFF03DAC5), // Teal
    Color(0xFF3700B3), // Deep Purple
    Color(0xFF018786), // Dark Teal
    Color(0xFF03A9F4), // Light Blue
    Color(0xFF2196F3), // Blue
    Color(0xFF8BC34A), // Light Green
    Color(0xFFCDDC39), // Lime
    Color(0xFFFFC107), // Amber
    Color(0xFFFF5722), // Deep Orange
    Color(0xFFFF9800), // Orange
    Color(0xFF9C27B0), // Purple
    Color(0xFFE91E63), // Pink
    Color(0xFFF44336), // Red
    Color(0xFF009688), // Green
    Color(0xFF795548), // Brown
    Color(0xFF607D8B), // Blue Grey
    Color(0xFF9E9E9E), // Grey
    Color(0xFFFFEB3B)  // Yellow
)


data class AppUsageData(
    val packageName: String,
    val totalTimeInForeground: Long
)

data class DailyStatistics(
    val date: String,
    val apps: List<AppUsageData>
)

// 他のサポート関数
fun formatTime(timeInMillis: Long): String {
    val hours = timeInMillis / (60 * 60 * 1000)
    val minutes = (timeInMillis % (60 * 60 * 1000)) / (60 * 1000)
    return "$hours 時間 $minutes 分"
}

fun roundToNearest3Hours(timeInMillis: Long): Long {
    val threeHoursInMillis = 3 * 60 * 60 * 1000
    return ((timeInMillis + threeHoursInMillis - 1) / threeHoursInMillis) * threeHoursInMillis
}

fun LocalDate.formatWithDayOfWeek(): String {
    val dayOfWeek = this.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.JAPANESE).substring(0, 1)
    val formatter = DateTimeFormatter.ofPattern("yyyy年M月d日")
    return "${this.format(formatter)} ($dayOfWeek)"
}

fun LocalDate.formatWithoutDayOfWeek(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy年M月d日")
    return this.format(formatter)
}


fun calculateDailyUsage(dailyStatistics: List<DailyStatistics>, selectedDate: String): List<AppUsageData> {
    return dailyStatistics
        .filter { it.date == selectedDate }
        .flatMap { it.apps }
        .groupBy { it.packageName }
        .map { (packageName, usages) ->
            AppUsageData(
                packageName,
                usages.sumOf { it.totalTimeInForeground }
            )
        }
        .sortedByDescending { it.totalTimeInForeground } // 降順にソート
}

fun calculatePeriodUsage(dailyStatistics: List<DailyStatistics>, startDate: LocalDate, endDate: LocalDate): List<AppUsageData> {
    return dailyStatistics
        .filter {
            val date = LocalDate.parse(it.date)
            !date.isBefore(startDate) && !date.isAfter(endDate)
        }
        .flatMap { it.apps }
        .groupBy { it.packageName }
        .map { (packageName, usages) ->
            AppUsageData(
                packageName,
                usages.sumOf { it.totalTimeInForeground }
            )
        }
        .sortedByDescending { it.totalTimeInForeground } // 降順にソート
}



@OptIn(ExperimentalPagerApi::class)
@Composable
fun UsageStatisticsScreen(dailyStatistics: List<DailyStatistics>) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val periods = listOf("日付指定", "期間指定")
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showPeriodPicker by remember { mutableStateOf(false) }
    var periodStartDate by remember { mutableStateOf(LocalDate.now()) }
    var periodEndDate by remember { mutableStateOf(LocalDate.now()) }

    var mainAppsCount by remember { mutableStateOf(5) }
    var showDropdownDialog by remember { mutableStateOf(false) }

    val usageData by remember(dailyStatistics, selectedDate, periodStartDate, periodEndDate, pagerState.currentPage) {
        derivedStateOf {
            when (pagerState.currentPage) {
                0 -> calculateDailyUsage(dailyStatistics, selectedDate.toString())
                1 -> calculatePeriodUsage(dailyStatistics, periodStartDate, periodEndDate)
                else -> calculateDailyUsage(dailyStatistics, selectedDate.toString())
            }
        }
    }

    val (mainUsageData, otherUsageData) = getMainAndOtherUsageData(usageData, mainAppsCount)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.hsl(300F, 0.1F, 0.9F),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            periods.forEachIndexed { index, period ->
                Tab(
                    text = { Text(period) },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(index)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalPager(
            count = periods.size,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        )  {
                            IconButton(onClick = { selectedDate = selectedDate.minusDays(1) }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "前の日")
                            }
                            TextButton(onClick = { showDatePicker = true }) {
                                Text(
                                    text = selectedDate.formatWithDayOfWeek(),
                                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 22.sp),
                                    color = Color(0xFF3700B3)
                                )
                            }
                            // 今日の日付ではない場合のみ右矢印を表示
                            if (selectedDate != LocalDate.now()) {
                                IconButton(onClick = { selectedDate = selectedDate.plusDays(1) }) {
                                    Icon(Icons.Default.ArrowForward, contentDescription = "次の日")
                                }
                            }else {
                                // スペースを保持するための透明なアイコン
                                IconButton(onClick = {}, enabled = false) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        tint = Color.Transparent
                                    )
                                }
                            }
                        }
                        if (showDatePicker) {
                            DatePickerDialog(
                                selectedDate = selectedDate,
                                onDateSelected = { newDate ->
                                    selectedDate = newDate
                                    showDatePicker = false
                                },
                                onDismissRequest = { showDatePicker = false }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ボタンを押してドロップダウンメニューを含むダイアログを表示
                        Button(
                            onClick = { showDropdownDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("アプリ数を選択 : $mainAppsCount", color = Color.White)
                        }

                        if (showDropdownDialog) {
                            Dialog(onDismissRequest = { showDropdownDialog = false }) {
                                Surface(
                                    shape = MaterialTheme.shapes.medium,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("表示する主要アプリの数", style = MaterialTheme.typography.titleMedium)
                                        LazyColumn {
                                            items(10) { index ->
                                                val i = index + 1
                                                DropdownMenuItem(
                                                    text = { Text(i.toString(), style = MaterialTheme.typography.bodyLarge) },
                                                    onClick = {
                                                        mainAppsCount = i
                                                        showDropdownDialog = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        AppUsageChart(mainUsageData = mainUsageData, otherUsageData = otherUsageData)
                    }
                }
                1 -> {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(onClick = { showPeriodPicker = true }) {
                                Text(
                                    text = "${periodStartDate.formatWithoutDayOfWeek()} 〜 ${periodEndDate.formatWithoutDayOfWeek()}",
                                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 22.sp),
                                    color = Color(0xFF3700B3)
                                )
                            }
                        }
                        if (showPeriodPicker) {
                            PeriodPickerDialog(
                                selectedStartDate = periodStartDate,
                                selectedEndDate = periodEndDate,
                                onStartDateSelected = { newDate ->
                                    periodStartDate = newDate
                                },
                                onEndDateSelected = { newDate ->
                                    periodEndDate = newDate
                                },
                                onConfirm = { showPeriodPicker = false },
                                onDismissRequest = { showPeriodPicker = false }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ボタンを押してドロップダウンメニューを含むダイアログを表示
                        Button(
                            onClick = { showDropdownDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("アプリ数を選択 : $mainAppsCount", color = Color.White)
                        }

                        if (showDropdownDialog) {
                            Dialog(onDismissRequest = { showDropdownDialog = false }) {
                                Surface(
                                    shape = MaterialTheme.shapes.medium,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("表示する主要アプリの数", style = MaterialTheme.typography.titleMedium)
                                        LazyColumn {
                                            items(10) { index ->
                                                val i = index + 1
                                                DropdownMenuItem(
                                                    text = { Text(i.toString(), style = MaterialTheme.typography.bodyLarge) },
                                                    onClick = {
                                                        mainAppsCount = i
                                                        showDropdownDialog = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        AppUsageChart(mainUsageData = mainUsageData, otherUsageData = otherUsageData)
                    }
                }
            }
        }
    }
}


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

            // 境界線を描画
//         drawRect(
//             color = Color.Gray, // 境界線の色
//             topLeft = Offset(0f, 0f),
//             size = Size(barWidth, size.height),
//             style = Stroke(width = 4f) // 境界線の幅を指定
//         )

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

    var canvasSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }

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


fun getMainAndOtherUsageData(usageData: List<AppUsageData>, mainAppsCount: Int): Pair<List<AppUsageData>, AppUsageData?> {
    val mainUsageData = usageData.take(mainAppsCount)
    val otherUsageData = if (usageData.size > mainAppsCount) {
        val otherTotalTime = usageData.drop(mainAppsCount).sumOf { it.totalTimeInForeground }
        AppUsageData("その他", otherTotalTime)
    } else {
        null
    }
    return Pair(mainUsageData, otherUsageData)
}


@Preview
@Composable
fun UsageStatisticsScreenPreview() {
    val dailyStatistics = listOf(
        DailyStatistics(
            "2024-01-01",
            listOf(
                AppUsageData("App1", 5000L),
                AppUsageData("App2", 3000L),
                AppUsageData("App3", 2000L),
                AppUsageData("App4", 1000L),
                AppUsageData("App5", 500L)
            )
        )
    )
    UsageStatisticsScreen(dailyStatistics)
}

