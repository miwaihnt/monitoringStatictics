package com.example.myapplication2

import android.graphics.Paint
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin


// カスタムカラーの定義
val colors = listOf(
    Color(0xFFBB86FC), // Light Purple
    Color(0xFF6200EE), // Purple
    Color(0xFF03DAC5), // Teal
    Color(0xFF3700B3), // Deep Purple
    Color(0xFF018786), // Dark Teal
    Color(0xFF03A9F4), // Light Blue
    Color(0xFF2196F3)  // Blue
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

fun calculateYearlyUsage(dailyStatistics: List<DailyStatistics>, selectedYear: String): List<AppUsageData> {
    return dailyStatistics
        .filter { it.date.startsWith(selectedYear) }
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

fun calculateMonthlyUsage(dailyStatistics: List<DailyStatistics>, selectedMonth: String): List<AppUsageData> {
    return dailyStatistics
        .filter { it.date.startsWith(selectedMonth) }
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

fun calculateWeeklyUsage(dailyStatistics: List<DailyStatistics>, selectedWeek: String): List<AppUsageData> {
    val weekFields = WeekFields.of(Locale.getDefault())
    return dailyStatistics
        .filter {
            val date = LocalDate.parse(it.date)
            val weekOfYear = date.get(weekFields.weekOfWeekBasedYear())
            selectedWeek == "${date.year}-W$weekOfYear"
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
    val periods = listOf("日毎", "期間指定")
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showPeriodPicker by remember { mutableStateOf(false) }
    var periodStartDate by remember { mutableStateOf(LocalDate.now()) }
    var periodEndDate by remember { mutableStateOf(LocalDate.now()) }

    val usageData by remember(dailyStatistics, selectedDate, periodStartDate, periodEndDate) {
        derivedStateOf {
            when (pagerState.currentPage) {
                0 -> calculateDailyUsage(dailyStatistics, selectedDate.toString())
                1 -> calculatePeriodUsage(dailyStatistics, periodStartDate, periodEndDate)
                else -> calculateDailyUsage(dailyStatistics, selectedDate.toString())
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth()
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

        when (pagerState.currentPage) {
            0 -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { selectedDate = selectedDate.minusDays(1) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "前の日")
                    }
                    TextButton(onClick = { showDatePicker = true }) {
                        Text("選択された日付: $selectedDate")
                    }
                    IconButton(onClick = { selectedDate = selectedDate.plusDays(1) }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "次の日")
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
            }
            1 -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { showPeriodPicker = true }) {
                        Text("選択された期間: $periodStartDate 〜 $periodEndDate")
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
            }
        }

        HorizontalPager(
            count = periods.size,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            AppUsageChart(appUsageData = usageData)
        }
    }
}


@Composable
fun AppUsageChart(appUsageData: List<AppUsageData>) {
    var showDialog by remember { mutableStateOf(false) }
    var dialogText by remember { mutableStateOf("") }

    // グラフの最大値を計算
    val maxTime = appUsageData.maxOfOrNull { it.totalTimeInForeground } ?: 1L
    val roundedMaxTime = roundToNearest3Hours(maxTime)

    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        item {
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

            Box(modifier = Modifier.fillMaxWidth()) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
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
        }

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

        item {
            Spacer(modifier = Modifier.height(16.dp))

            // 円グラフを表示する部分
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                PieChart(
                    appUsageData = appUsageData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )

                // 凡例を表示
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    appUsageData.forEachIndexed { index, data ->
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
                            Text(
                                text = data.packageName,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        if (showDialog) {
            item {
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


@Composable
fun PieChart(
    appUsageData: List<AppUsageData>,
    modifier: Modifier = Modifier
) {
    val totalUsageTime = appUsageData.sumOf { it.totalTimeInForeground }
    val sweepAngles = appUsageData.map { 360f * (it.totalTimeInForeground / totalUsageTime.toFloat()) }
    val percentages = appUsageData.map { (it.totalTimeInForeground / totalUsageTime.toFloat() * 100).toInt() }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            var startAngle = -90f  // 12時方向をスタートに設定

            sweepAngles.forEachIndexed { index, sweepAngle ->
                val sweepAngleRad = Math.toRadians(sweepAngle.toDouble() / 2.0 + startAngle.toDouble()).toFloat()
                val radius = size.minDimension / 2 / 1.5f  // 円グラフの半径
                val x = size.width / 2 + cos(sweepAngleRad) * radius
                val y = size.height / 2 + sin(sweepAngleRad) * radius

                drawArc(
                    color = colors[index % colors.size],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true
                )

                if (percentages[index] > 10) {
                    drawContext.canvas.nativeCanvas.drawText(
                        "${percentages[index]}%",
                        x,
                        y,
                        Paint().apply {
                            color = Color.Black.toArgb()
                            textAlign = Paint.Align.CENTER
                            textSize = 40f  // フォントサイズを40fに設定
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


//「年毎」「月毎」「週毎」「日毎」「期間指定」の五種類バージョン

//  @OptIn(ExperimentalPagerApi::class)
//  @Composable
//
//  fun UsageStatisticsScreen(dailyStatistics: List<DailyStatistics>) {
//      val pagerState = rememberPagerState()
//      val coroutineScope = rememberCoroutineScope()
//      val periods = listOf("年毎", "月毎", "週毎", "日毎", "期間指定")
//      var selectedYear by remember { mutableStateOf(LocalDate.now().year.toString()) }
//      var selectedMonth by remember { mutableStateOf(YearMonth.now()) }
//      var selectedWeek by remember {
//          mutableStateOf(Pair(getCurrentWeekValue(LocalDate.now()), getCurrentWeekLabel(LocalDate.now())))
//      }
//      var selectedDate by remember { mutableStateOf(LocalDate.now()) }
//      var showYearPicker by remember { mutableStateOf(false) }
//      var showMonthPicker by remember { mutableStateOf(false) }
//      var showWeekPicker by remember { mutableStateOf(false) }
//      var showDatePicker by remember { mutableStateOf(false) }
//      var showPeriodPicker by remember { mutableStateOf(false) }
//      var periodStartDate by remember { mutableStateOf(LocalDate.now()) }
//      var periodEndDate by remember { mutableStateOf(LocalDate.now()) }
//
//      val usageData by remember {
//          derivedStateOf {
//              when (pagerState.currentPage) {
//                  0 -> calculateYearlyUsage(dailyStatistics, selectedYear)
//                  1 -> calculateMonthlyUsage(dailyStatistics, selectedMonth.toString())
//                  2 -> calculateWeeklyUsage(dailyStatistics, selectedWeek.first)
//                  3 -> calculateDailyUsage(dailyStatistics, selectedDate.toString())
//                  4 -> calculatePeriodUsage(dailyStatistics, periodStartDate, periodEndDate)
//                  else -> calculateDailyUsage(dailyStatistics, selectedDate.toString())
//              }
//          }
//      }
//
//      Column(
//          modifier = Modifier
//              .fillMaxSize()
//              .padding(16.dp)
//      ) {
//          TabRow(
//              selectedTabIndex = pagerState.currentPage,
//              modifier = Modifier.fillMaxWidth()
//          ) {
//              periods.forEachIndexed { index, period ->
//                  Tab(
//                      text = { Text(period) },
//                      selected = pagerState.currentPage == index,
//                      onClick = {
//                          coroutineScope.launch {
//                              pagerState.scrollToPage(index)
//                          }
//                      }
//                  )
//              }
//          }
//
//          when (pagerState.currentPage) {
//              0 -> {
//                  Row(verticalAlignment = Alignment.CenterVertically) {
//                      IconButton(onClick = { selectedYear = (selectedYear.toInt() - 1).toString() }) {
//                          Icon(Icons.Default.ArrowBack, contentDescription = "前の年")
//                      }
//                      TextButton(onClick = { showYearPicker = true }) {
//                          Text("選択された年: $selectedYear")
//                      }
//                      IconButton(onClick = { selectedYear = (selectedYear.toInt() + 1).toString() }) {
//                          Icon(Icons.Default.ArrowForward, contentDescription = "次の年")
//                      }
//                  }
//                  if (showYearPicker) {
//                      YearPickerDialog(
//                          selectedYear = selectedYear,
//                          onYearSelected = { selectedYear = it },
//                          onDismissRequest = { showYearPicker = false }
//                      )
//                  }
//              }
//              1 -> {
//                  Row(verticalAlignment = Alignment.CenterVertically) {
//                      IconButton(onClick = { selectedMonth = selectedMonth.minusMonths(1) }) {
//                          Icon(Icons.Default.ArrowBack, contentDescription = "前の月")
//                      }
//                      TextButton(onClick = { showMonthPicker = true }) {
//                          Text("選択された月: $selectedMonth")
//                      }
//                      IconButton(onClick = { selectedMonth = selectedMonth.plusMonths(1) }) {
//                          Icon(Icons.Default.ArrowForward, contentDescription = "次の月")
//                      }
//                  }
//                  if (showMonthPicker) {
//                      MonthPickerDialog(
//                          displayedYear = selectedMonth.year,
//                          selectedMonth = selectedMonth,
//                          onMonthSelected = { selectedMonth = it },
//                          onDismissRequest = { showMonthPicker = false }
//                      )
//                  }
//              }
//              2 -> {
//                  Row(verticalAlignment = Alignment.CenterVertically) {
//                      IconButton(onClick = { selectedWeek = getPreviousWeek(selectedWeek.first) }) {
//                          Icon(Icons.Default.ArrowBack, contentDescription = "前の週")
//                      }
//                      TextButton(onClick = { showWeekPicker = true }) {
//                          Text("選択された週: ${selectedWeek.second}")
//                      }
//                      IconButton(onClick = { selectedWeek = getNextWeek(selectedWeek.first) }) {
//                          Icon(Icons.Default.ArrowForward, contentDescription = "次の週")
//                      }
//                  }
//                  if (showWeekPicker) {
//                      WeekPickerDialog(
//                          displayedYear = selectedWeek.first.substring(0, 4).toInt(), // 現在の週の年を表示年に設定
//                          selectedWeek = selectedWeek,
//                          onWeekSelected = { weekValue, weekLabel ->
//                              selectedWeek = Pair(weekValue, weekLabel)
//                          },
//                          onDismissRequest = { showWeekPicker = false }
//                      )
//                  }
//              }
//              3 -> {
//                  Row(verticalAlignment = Alignment.CenterVertically) {
//                      IconButton(onClick = { selectedDate = selectedDate.minusDays(1) }) {
//                          Icon(Icons.Default.ArrowBack, contentDescription = "前の日")
//                      }
//                      TextButton(onClick = { showDatePicker = true }) {
//                          Text("選択された日付: $selectedDate")
//                      }
//                      IconButton(onClick = { selectedDate = selectedDate.plusDays(1) }) {
//                          Icon(Icons.Default.ArrowForward, contentDescription = "次の日")
//                      }
//                  }
//                  if (showDatePicker) {
//                      DatePickerDialog(
//                          selectedDate = selectedDate,
//                          onDateSelected = { newDate ->
//                              selectedDate = newDate
//                              showDatePicker = false
//                          },
//                          onDismissRequest = { showDatePicker = false }
//                      )
//                  }
//              }
//              4 -> {
//                  Row(verticalAlignment = Alignment.CenterVertically) {
//                      TextButton(onClick = { showPeriodPicker = true }) {
//                          Text("選択された期間: $periodStartDate 〜 $periodEndDate")
//                      }
//                  }
//                  if (showPeriodPicker) {
//                      PeriodPickerDialog(
//                          selectedStartDate = periodStartDate,
//                          selectedEndDate = periodEndDate,
//                          onStartDateSelected = { newDate ->
//                              periodStartDate = newDate
//                          },
//                          onEndDateSelected = { newDate ->
//                              periodEndDate = newDate
//                          },
//                          onConfirm = { /* Confirm button logic if needed */ },
//                          onDismissRequest = { showPeriodPicker = false }
//                      )
//                  }
//              }
//          }
//
//          HorizontalPager(
//              count = periods.size,
//              state = pagerState,
//              modifier = Modifier.fillMaxSize()
//          ) { page ->
//              AppUsageChart(appUsageData = usageData)
//          }
//      }
//  }
