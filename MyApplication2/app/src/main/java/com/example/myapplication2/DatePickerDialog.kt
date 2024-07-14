package com.example.myapplication2

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Calendar
import java.util.Locale


@Composable
fun YearPickerDialog(
    selectedYear: String,
    onYearSelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("年を選択") },
        text = {
            LazyColumn {
                items((2000..2050).toList()) { year ->
                    Text(
                        text = year.toString(),
                        modifier = Modifier
                            .clickable {
                                onYearSelected(year.toString())
                                onDismissRequest()
                            }
                            .padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("閉じる")
            }
        }
    )
}

@Composable
fun MonthPickerDialog(
    displayedYear: Int,
    selectedMonth: YearMonth,
    onMonthSelected: (YearMonth) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("月を選択") },
        text = {
            val monthsInYear = (1..12).map { month ->
                YearMonth.of(displayedYear, month)
            }
            LazyColumn {
                items(monthsInYear) { yearMonth ->
                    Text(
                        text = yearMonth.toString(),
                        modifier = Modifier
                            .clickable {
                                onMonthSelected(yearMonth)
                                onDismissRequest()
                            }
                            .padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("閉じる")
            }
        }
    )
}

@Composable
fun WeekPickerDialog(
    displayedYear: Int,
    selectedWeek: Pair<String, String>,
    onWeekSelected: (String, String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val weekFields = WeekFields.of(Locale.getDefault())
    val firstDayOfYear = LocalDate.of(displayedYear, 1, 1)
    val lastDayOfYear = LocalDate.of(displayedYear, 12, 31)
    val weeksInYear = generateSequence(firstDayOfYear) { it.plusWeeks(1) }
        .takeWhile { it.isBefore(lastDayOfYear) || it.isEqual(lastDayOfYear) }
        .map { firstDayOfWeek ->
            val lastDayOfWeek = firstDayOfWeek.plusDays(6).let {
                if (it.isAfter(lastDayOfYear)) lastDayOfYear else it
            }
            Pair(
                "${firstDayOfWeek.year}-W${firstDayOfWeek.get(weekFields.weekOfYear())}",
                "${firstDayOfWeek.monthValue}月${firstDayOfWeek.dayOfMonth}日〜${lastDayOfWeek.monthValue}月${lastDayOfWeek.dayOfMonth}日"
            )
        }
        .toList()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("週を選択") },
        text = {
            LazyColumn {
                items(weeksInYear) { (weekValue, weekLabel) ->
                    Text(
                        text = weekLabel,
                        modifier = Modifier
                            .clickable {
                                onWeekSelected(weekValue, weekLabel)
                                onDismissRequest()
                            }
                            .padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("閉じる")
            }
        }
    )
}

fun getCurrentWeekLabel(date: LocalDate): String {
    val weekFields = WeekFields.of(Locale.getDefault())
    val weekStart = date.with(weekFields.weekOfWeekBasedYear(), date.get(weekFields.weekOfWeekBasedYear()).toLong())
        .with(weekFields.dayOfWeek(), weekFields.firstDayOfWeek.value.toLong())
    val weekEnd = weekStart.plusDays(6) // 週の終わりの日を取得
    val year = weekStart.year
    return "${year}年 ${weekStart.monthValue}月${weekStart.dayOfMonth}日〜${weekEnd.monthValue}月${weekEnd.dayOfMonth}日"
}

fun getCurrentWeekValue(date: LocalDate): String {
    val weekFields = WeekFields.of(Locale.getDefault())
    val currentYear = date.year
    val weekOfYear = date.get(weekFields.weekOfWeekBasedYear())
    return "${currentYear}-W${weekOfYear}"
}

fun getPreviousWeek(currentWeek: String): Pair<String, String> {
    Log.d("WeekCalculation", "Current Week before processing: $currentWeek")
    return try {
        Log.d("WeekCalculation", "Current Week: $currentWeek")
        if (currentWeek.isEmpty()) throw IllegalArgumentException("Current week is empty")

        val weekFields = WeekFields.of(Locale.getDefault())
        val year = currentWeek.substringBefore("-W").toInt()
        val week = currentWeek.substringAfter("-W").toInt()
        val date = LocalDate.of(year, 1, 1)
            .with(weekFields.weekOfWeekBasedYear(), week.toLong())
            .with(weekFields.dayOfWeek(), 1)
        val previousWeekStart = date.minusWeeks(1)
        val previousWeekValue = "${previousWeekStart.year}-W${previousWeekStart.get(weekFields.weekOfWeekBasedYear())}"
        val previousWeekLabel = getCurrentWeekLabel(previousWeekStart)
        Pair(previousWeekValue, previousWeekLabel)
    } catch (e: Exception) {
        Log.e("WeekCalculation", "Error calculating previous week: ${e.message}")
        Pair(currentWeek, "エラー")
    }
}

fun getNextWeek(currentWeek: String): Pair<String, String> {
    Log.d("WeekCalculation", "Current Week before processing: $currentWeek")
    return try {
        Log.d("WeekCalculation", "Current Week: $currentWeek")
        if (currentWeek.isEmpty()) throw IllegalArgumentException("Current week is empty")

        val weekFields = WeekFields.of(Locale.getDefault())
        val year = currentWeek.substringBefore("-W").toInt()
        val week = currentWeek.substringAfter("-W").toInt()
        val date = LocalDate.of(year, 1, 1)
            .with(weekFields.weekOfWeekBasedYear(), week.toLong())
            .with(weekFields.dayOfWeek(), 1)
        val nextWeekStart = date.plusWeeks(1)
        val nextWeekValue = "${nextWeekStart.year}-W${nextWeekStart.get(weekFields.weekOfWeekBasedYear())}"
        val nextWeekLabel = getCurrentWeekLabel(nextWeekStart)
        Pair(nextWeekValue, nextWeekLabel)
    } catch (e: Exception) {
        Log.e("WeekCalculation", "Error calculating next week: ${e.message}")
        Pair(currentWeek, "エラー")
    }
}

@Composable
fun DatePickerDialog(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    calendar.set(selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth)

    // Android の DatePickerDialog を使用して日付を選択
    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val newDate = LocalDate.of(year, month + 1, dayOfMonth)
            onDateSelected(newDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    DisposableEffect(Unit) {
        // コンポーネントが破棄されたときにダイアログを閉じる
        datePickerDialog.show()
        onDispose {
            datePickerDialog.dismiss()
        }
    }
}


@Composable
fun PeriodPickerDialog(
    selectedStartDate: LocalDate,
    selectedEndDate: LocalDate,
    onStartDateSelected: (LocalDate) -> Unit,
    onEndDateSelected: (LocalDate) -> Unit,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit
) {
    var startDateInput by remember { mutableStateOf(selectedStartDate.toString().replace("-", "")) }
    var endDateInput by remember { mutableStateOf(selectedEndDate.toString().replace("-", "")) }
    val startDateError by remember { derivedStateOf { !startDateInput.matches(Regex("\\d{8}")) } }
    val endDateError by remember { derivedStateOf { !endDateInput.matches(Regex("\\d{8}")) } }

    // 現在の日付を取得し、プレースホルダーテキストとして利用
    val currentDate = LocalDate.now().toString().replace("-", "")

    AlertDialog(
        onDismissRequest = {
            onDismissRequest()
        },
        title = { Text("期間を選択") },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("開始日 (YYYYMMDD): ")
                    TextField(
                        value = startDateInput,
                        onValueChange = {
                            if (it.matches(Regex("\\d*"))) startDateInput = it
                        },
                        // placeholder = { Text(currentDate) }, // 例示テキストを設定
                        isError = startDateError
                    )
                }
                if (startDateError) {
                    Text("正しい日付形式を入力してください ", color = MaterialTheme.colorScheme.error)
                    Text("(例: ${currentDate})", color = MaterialTheme.colorScheme.error)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("終了日 (YYYYMMDD): ")
                    TextField(
                        value = endDateInput,
                        onValueChange = {
                            if (it.matches(Regex("\\d*"))) endDateInput = it
                        },
                        // placeholder = { Text(currentDate) }, // 例示テキストを設定
                        isError = endDateError
                    )
                }
                if (endDateError) {
                    Text("正しい日付形式を入力してください (例: ${currentDate})", color = MaterialTheme.colorScheme.error)
                    Text("(例: ${currentDate})", color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (!startDateError && !endDateError) {
                    onStartDateSelected(LocalDate.parse(startDateInput.substring(0, 4) + "-" + startDateInput.substring(4, 6) + "-" + startDateInput.substring(6, 8)))
                    onEndDateSelected(LocalDate.parse(endDateInput.substring(0, 4) + "-" + endDateInput.substring(4, 6) + "-" + endDateInput.substring(6, 8)))
                    onConfirm()
                    onDismissRequest()
                }
            }) {
                Text("確認")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("閉じる")
            }
        }
    )
}

@Composable
fun MainScreen() {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        // 日付選択ボタン
        TextButton(onClick = { showDatePicker = true }) {
            Text("日付を選択")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 選択された日付の表示
        Text(text = "選択された日付: $selectedDate")

        // 日付選択ダイアログ
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
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainScreen()
}