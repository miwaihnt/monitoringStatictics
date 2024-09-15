package com.example.myapplication2.View2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale



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
    val appName: String,
    val totalTimeInForeground: Long,
    val lastTime: String? = null,  // nullable に変更
    val iconUrl: String? = null
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
        .groupBy { it.appName }  // appName でグルーピング
        .map { (appName, usages) ->
            val totalTimeInForeground = usages.sumOf { it.totalTimeInForeground }

            // lastTime が null の場合でも処理できるように null-safe 比較を追加
            val latestUsage = usages.maxByOrNull { it.lastTime ?: "" } // 最新のアイコンURLを持つ使用データを取得

            AppUsageData(
                appName = appName,
                totalTimeInForeground = totalTimeInForeground,
                lastTime = latestUsage?.lastTime ?: "", // latestUsage が null なら空文字をセット
                iconUrl = latestUsage?.iconUrl // 最新の iconUrl をセット
            )
        }
        .sortedByDescending { it.totalTimeInForeground } // 合計使用時間で降順ソート
}


fun calculatePeriodUsage(
    dailyStatistics: List<DailyStatistics>,
    startDate: LocalDate,
    endDate: LocalDate
): List<AppUsageData> {
    return dailyStatistics
        .filter {
            val date = LocalDate.parse(it.date)
            !date.isBefore(startDate) && !date.isAfter(endDate)
        }
        .flatMap { it.apps }
        .groupBy { it.appName }  // appName でグルーピング
        .map { (appName, usages) ->
            val totalTimeInForeground = usages.sumOf { it.totalTimeInForeground }

            // lastTime が null の場合でも処理できるように null-safe 比較を追加
            val latestUsage = usages.maxByOrNull { it.lastTime ?: "" } // 最新のアイコンURLを持つ使用データを取得

            AppUsageData(
                appName = appName,
                totalTimeInForeground = totalTimeInForeground,
                lastTime = latestUsage?.lastTime ?: "", // latestUsage が null なら空文字をセット
                iconUrl = latestUsage?.iconUrl // 最新の iconUrl をセット
            )
        }
        .sortedByDescending { it.totalTimeInForeground } // 合計使用時間で降順ソート
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

