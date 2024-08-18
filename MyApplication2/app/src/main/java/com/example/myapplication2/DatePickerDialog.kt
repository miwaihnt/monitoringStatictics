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
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Calendar
import java.util.Locale


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

    // キャンセルリスナーを追加して、ダイアログがキャンセルされたときに `onDismissRequest` を呼び出す
    datePickerDialog.setOnCancelListener {
        onDismissRequest()
    }


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
                    Text("正しい日付形式を入力してください ", color = MaterialTheme.colorScheme.error)
                    Text("(例: ${currentDate})", color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!startDateError && !endDateError) {
                        val startDate = LocalDate.parse(startDateInput, DateTimeFormatter.ofPattern("yyyyMMdd"))
                        val endDate = LocalDate.parse(endDateInput, DateTimeFormatter.ofPattern("yyyyMMdd"))
                        onStartDateSelected(startDate)
                        onEndDateSelected(endDate)
                        onConfirm()
                    }
                },
                enabled = !startDateError && !endDateError
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text("キャンセル")
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